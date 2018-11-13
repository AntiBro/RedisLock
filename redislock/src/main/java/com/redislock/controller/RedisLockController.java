package com.redislock.controller;

import com.redislock.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author huaili
 * @Date 2018/11/13 13:54
 * @Description restful接口用于修改redislock的配置参数
 **/
@RestController
public class RedisLockController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @PostMapping("/setlockConfig")
    public String updateConfig(int size,int timeout){

        if(size>0){
            redisTemplate.opsForValue().set(RedisLock.REDIS_LOCK_POOL_KEY_NAME,size+"");
        }

        if(timeout>0){
            redisTemplate.opsForValue().set(RedisLock.REDIS_LOCK_TIME_OUT,timeout+"");
        }

        return "success updated";
    }
}
