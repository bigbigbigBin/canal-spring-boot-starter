package com.stdp.start.canal.transponder;

public class MessageTransponders {
    public static TransponderFactory defaultMessageTransponder() {
        return new DefaultTransponderFactory();
    }
}
