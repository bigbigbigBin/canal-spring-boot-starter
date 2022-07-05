package com.stdp.start.canal.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "canal.client")
public class CanalConfig {

    private Map<String, Instance> instances = new LinkedHashMap<>();

    public Map<String, Instance> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, Instance> instances) {
        this.instances = instances;
    }


    public static class Instance {

        /**
         * is cluster-mod
         */
        private boolean clusterEnabled;

        /**
         * zookeeper server address
         */
        private Set<String> zookeeperAddress = new LinkedHashSet<>();

        /**
         * canal server host
         */
        private String host = "127.0.0.1";

        /**
         * canal server port
         */
        private int port = 11111;

        /**
         * canal user name
         */
        private String userName = "";

        /**
         * canal user password
         */
        private String password = "";

        /**
         * size when get message from the canal server
         */
        private int batchSize = 1000;

        /**
         *  订阅设置
         */
        private String filter;

        /**
         * retry count when error occurred
         */
        private int retryCount = 5;

        /**
         * interval of the message-acquiring
         */
        private long acquireInterval = 1000;

        public Instance() {
        }

        public boolean isClusterEnabled() {
            return clusterEnabled;
        }

        public void setClusterEnabled(boolean clusterEnabled) {
            this.clusterEnabled = clusterEnabled;
        }

        public Set<String> getZookeeperAddress() {
            return zookeeperAddress;
        }

        public void setZookeeperAddress(Set<String> zookeeperAddress) {
            this.zookeeperAddress = zookeeperAddress;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public long getAcquireInterval() {
            return acquireInterval;
        }

        public void setAcquireInterval(long acquireInterval) {
            this.acquireInterval = acquireInterval;
        }
    }
}
