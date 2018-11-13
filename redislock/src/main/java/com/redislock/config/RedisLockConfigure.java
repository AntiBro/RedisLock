package com.redislock.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author huaili
 * @Date 2018/11/13 14:08
 * @Description TODO
 **/
@Configuration
@EnableConfigurationProperties({RedisLockProperties.class})
@ComponentScan("com.redislock")
public class RedisLockConfigure implements InitializingBean {
    private static Logger log= Logger.getLogger(RedisLockConfigure.class);
    @Autowired
    RedisLockProperties redisLockProperties;
    @Autowired
    StringRedisTemplate redisTemplate;
    public RedisLockConfigure(){
        log.info("loading redisLock config...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {


    }
}
