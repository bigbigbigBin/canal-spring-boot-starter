package com.stdp.start.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.stdp.start.canal.config.CanalConfig;
import com.stdp.start.canal.event.CanalEventListener;
import com.stdp.start.canal.transponder.TransponderFactory;
import com.stdp.start.canal.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleCanalClient extends AbstractCanalClient {

    private final static Logger logger = LoggerFactory.getLogger(SimpleCanalClient.class);

    /**
     * listeners which are used by implementing the Interface
     */
    private final List<CanalEventListener> listeners = new ArrayList<>();

    /**
     * executor
     */
    private ThreadPoolExecutor executor;

    /**
     * TransponderFactory
     */
    private final TransponderFactory factory;


    public SimpleCanalClient(CanalConfig canalConfig, TransponderFactory factory) {
        super(canalConfig);
        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), Executors.defaultThreadFactory());
        this.factory = factory;
        initListeners();
    }


    private void initListeners() {
        logger.info("{}: initializing the listeners....", Thread.currentThread().getName());
        List<CanalEventListener> list = BeanUtil.getBeansOfType(CanalEventListener.class);
        if (list != null) {
            listeners.addAll(list);
        }
        //todo annotation



        logger.info("{}: initializing the listeners end.", Thread.currentThread().getName());
        // todo
        if (logger.isWarnEnabled() && listeners.isEmpty() ) {
            logger.warn("{}: No listener found in context! ", Thread.currentThread().getName());
        }
    }

    @Override
    protected void process(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> config) {
        executor.submit(factory.newTransponder(config.getValue(), connector, listeners));
    }
}
