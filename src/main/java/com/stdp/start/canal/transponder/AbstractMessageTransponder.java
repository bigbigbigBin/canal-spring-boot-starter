package com.stdp.start.canal.transponder;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stdp.start.canal.config.CanalConfig;
import com.stdp.start.canal.event.CanalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractMessageTransponder implements MessageTransponder {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageTransponder.class);

    /**
     * 系统定义的配置
     */
    private CanalConfig.Instance instanceConfig;

    /**
     * canal数据操作客户端连接器
     */
    private CanalConnector connector;

    /**
     * canal转换器的运行状态
     */
    private volatile boolean isRunning = true;

    /**
     * 定义的 canal event listeners
     */
    private List<CanalEventListener> eventListeners = new ArrayList<>();

    public AbstractMessageTransponder(CanalConfig.Instance instanceConfig,
                                      CanalConnector connector,
                                      List<CanalEventListener> listeners) {
        Objects.requireNonNull(connector, "connector can not be null!");
        Objects.requireNonNull(instanceConfig, "canal client instance config can not be null!");
        this.instanceConfig = instanceConfig;
        this.connector = connector;
        if (listeners != null)
            this.eventListeners.addAll(listeners);
    }

    @Override
    public void run() {
        int errorCount = instanceConfig.getRetryCount();
        long interval = instanceConfig.getAcquireInterval();
        final String threadName = Thread.currentThread().getName();
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            try {
                Message message = connector.getWithoutAck(instanceConfig.getBatchSize());
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (logger.isDebugEnabled()) {
                    logger.debug("{}: Get message from canal server >>>>> size:{}", threadName, size);
                }

                if (size == 0 || batchId == -1) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("{}: Empty message... sleep for {} millis", threadName, interval);
                    }
                    Thread.sleep(interval);
                } else {
                    transformMessage(message);
                }
            } catch (CanalClientException calEx) {
                errorCount--;
                logger.error(threadName + ": Error occurred! ", calEx);
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException itrEx) {
                    errorCount = 0;
                }
            } catch (InterruptedException itrEx) {
                errorCount = 0;
                connector.rollback();
            } finally {
                if (errorCount <= 0) {
                    stop();
                    logger.info(threadName + "Stop the canal client");
                }
            }
        }
        stop();
        logger.info(threadName + "Stop the canal client");
    }

    /**
     * 转换canal message消息
     * @param message
     */
    @Override
    public void transformMessage(Message message) {
        List<CanalEntry.Entry> entries = message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            // 忽略掉的entryType
            List<CanalEntry.EntryType> ignoreEntryTypes = getIgnoreEntryTypes();
            if (ignoreEntryTypes != null && ignoreEntryTypes.stream().anyMatch(t -> entry.getEntryType() == t)) {
                continue;
            }

            // message row 每行变更数据的数据结构
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                throw new CanalClientException("ERROR ## parser of event has an error , data:" + entry.toString(), e);
            }

            // 处理DDL
            if (rowChange.hasIsDdl() && rowChange.getIsDdl()) {
                processDdl(rowChange);
                continue;
            }

            // 处理DML
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                distributeByImpl(rowChange.getEventType(), rowData);
            }
        }
    }

    /**
     * 获取应忽略掉的eventType list
     * 子类可以自定义具体类型
     * @return eventType list
     */
    protected List<CanalEntry.EntryType> getIgnoreEntryTypes() {
        return Collections.emptyList();
    }

    /**
     *处理ddl类型的数据
     * @param rowChange rowChange
     */
    protected void processDdl(CanalEntry.RowChange rowChange) {
    }


    /**
     * 分发数据给listener
     *
     * @param eventType canal定义的事件类型
     * @param rowData   canal定义的数据内容
     */
    protected void distributeByImpl(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        if (eventListeners != null) {
            for (CanalEventListener listener : eventListeners) {
                listener.onEvent(eventType, rowData);
            }
        }
    }


    public void stop() {
        isRunning = false;
    }
}
