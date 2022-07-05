package com.stdp.start.canal.transponder;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.stdp.start.canal.config.CanalConfig;
import com.stdp.start.canal.event.CanalEventListener;

import java.util.Arrays;
import java.util.List;

public class DefaultMessageTransponder extends AbstractMessageTransponder {

    public DefaultMessageTransponder(CanalConfig.Instance instanceConfig, CanalConnector connector, List<CanalEventListener> listeners) {
        super(instanceConfig, connector, listeners);
    }

    @Override
    protected List<CanalEntry.EntryType> getIgnoreEntryTypes() {
        return Arrays.asList(CanalEntry.EntryType.TRANSACTIONBEGIN, CanalEntry.EntryType.TRANSACTIONEND, CanalEntry.EntryType.HEARTBEAT);
    }
}
