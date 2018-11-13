package com.example.testmain;

import com.redislock.config.RedisLockConfigure;
import com.redislock.lock.RedisLock;
import org.apache.log4j.Logger;

/**
 * @Author huaili
 * @Date 2018/11/13 16:35
 * @Description TODO
 **/
public class TestThread implements Runnable{

    private static Logger log= Logger.getLogger(RedisLockConfigure.class);

    int count;
    RedisLock lock;
    public TestThread(int count,RedisLock lock){
        this.count=count;
        this.lock=lock;
    }

    private boolean  dowork(int i){
        if(lock.trylock()){
            log.info(Thread.currentThread().getName()+"thread ["+count +"] 第"+i+"次 lock success");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(lock.unlock()) {
                log.info(Thread.currentThread().getName()+"thread ["+count +"] 第"+i+"次 unlock success");
            }
            return true;
        }else
            return false;
    }

    @Override
    public void run() {
        if(!dowork(1))
        {
            log.info(Thread.currentThread().getName()+"第一次失败获得锁 thread ["+count +"] ，尝试第二次获取锁 sleep 3s");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!dowork(2)){
                log.info(Thread.currentThread().getName()+"第二次失败获得锁 thread ["+count +"]");
            }
        }
    }
}