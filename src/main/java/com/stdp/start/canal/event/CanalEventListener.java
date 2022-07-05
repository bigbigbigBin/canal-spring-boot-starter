package com.stdp.start.canal.event;


import com.alibaba.otter.canal.protocol.CanalEntry;

public interface CanalEventListener {

    /**
     * 当事件发生时，触发此方法
     * @param eventType   canal定义的事件类型
     * @param rowData     canal定义的数据内容
     */
    void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData);
}
