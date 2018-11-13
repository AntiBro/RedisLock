package com.redislock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author huaili
 * @Date 2018/11/13 14:09
 * @Description properties文件配置类
 **/
@ConfigurationProperties(prefix = "redis.lock.pool")
public class RedisLockProperties {


    private Integer size;
    private Integer timeout;

    public Integer getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "RedisLockProperties{size=["+size+"]; timeout=["+timeout+"]}";
    }
}
