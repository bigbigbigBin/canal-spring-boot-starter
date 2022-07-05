package com.stdp.start.canal.transponder;

import com.alibaba.otter.canal.client.CanalConnector;
import com.stdp.start.canal.config.CanalConfig;
import com.stdp.start.canal.event.CanalEventListener;

import java.util.List;

public class DefaultTransponderFactory implements TransponderFactory {
    @Override
    public MessageTransponder newTransponder(CanalConfig.Instance instanceConfig, CanalConnector connector, List<CanalEventListener> listeners) {
        return new DefaultMessageTransponder(instanceConfig, connector, listeners);
    }
}
