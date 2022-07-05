package com.stdp.start.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.stdp.start.canal.config.CanalConfig;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractCanalClient implements CanalClient {

    private volatile boolean running;

    private CanalConfig canalConfig;

    public AbstractCanalClient(CanalConfig canalConfig) {
        Objects.requireNonNull(canalConfig, "canalConfig can not be null!");
        this.canalConfig = canalConfig;
    }

    @Override
    public void start() {
        Map<String, CanalConfig.Instance> instanceMap = getConfig();
        for (Map.Entry<String, CanalConfig.Instance> instanceEntry : instanceMap.entrySet()) {
            CanalConnector instanceConnector = initInstanceConnector(instanceEntry);
            process(instanceConnector, instanceEntry);
        }
    }

    @Override
    public void stop() {
        setRunning(false);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    protected Map<String, CanalConfig.Instance> getConfig() {
        CanalConfig config = canalConfig;
        Map<String, CanalConfig.Instance> instanceMap;
        if (config != null && (instanceMap = config.getInstances()) != null && !instanceMap.isEmpty()) {
            return config.getInstances();
        } else {
            throw new CanalClientException("can not get the configuration of canal client!");
        }
    }

    /**
     * 初始化canal 客户端
     * @param instanceEntry
     * @return
     */
    private CanalConnector initInstanceConnector(Map.Entry<String, CanalConfig.Instance> instanceEntry) {
        CanalConfig.Instance instance = instanceEntry.getValue();
        CanalConnector connector;

        if (instance.isClusterEnabled()) {
            List<SocketAddress> addresses = new ArrayList<>();
            for (String s : instance.getZookeeperAddress()) {
                String[] entry = s.split(":");
                if (entry.length != 2) {
                    throw new CanalClientException("ERROR parsing canal zookeeper address:" + s);
                }
                addresses.add(new InetSocketAddress(entry[0], Integer.parseInt(entry[1])));
            }
            connector = CanalConnectors.newClusterConnector(addresses,
                    instanceEntry.getKey(), instance.getUserName(), instance.getPassword());

        } else {
            connector = CanalConnectors.newSingleConnector(
                    new InetSocketAddress(instance.getHost(), instance.getPort()),
                    instanceEntry.getKey(),
                    instance.getUserName(),
                    instance.getPassword()
                    );
        }
        connector.connect();

        if (!StringUtils.isEmpty(instance.getFilter())) {
            connector.subscribe(instance.getFilter());
        } else {
            connector.subscribe();
        }

        connector.rollback();
        return connector;
    }

    /**
     * 使用canal数据操作客户端，进行canal监听的数据的处理
     * 在处理的时候，可以自定义策略，比方使用线程池来处理
     * @param connector
     * @param config
     */
    protected abstract void process(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> config);
}
