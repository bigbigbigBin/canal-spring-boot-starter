package com.stdp.start.canal.transponder;

import com.alibaba.otter.canal.protocol.Message;

/**
 * 定义 canal message 转换器接口
 */
public interface MessageTransponder extends Runnable {

    /**
     * 转换canal message消息
     *
     * @param message
     */
    void transformMessage(Message message);
}
