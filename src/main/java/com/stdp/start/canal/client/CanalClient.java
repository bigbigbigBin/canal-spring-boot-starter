package com.stdp.start.canal.client;

public interface CanalClient {

    /**
     * 打开Canal客户端
     * 获取配置，并连接到CanalServer(1 : 1 or 1 : n)
     * 当有canal消息被获取到后，将消息传到listener去处理
     * */
    void start();

    /**
     * 关闭Canal客户端
     */
    void stop();

    /**
     * is running
     * @return yes or no
     */
    boolean isRunning();
}
