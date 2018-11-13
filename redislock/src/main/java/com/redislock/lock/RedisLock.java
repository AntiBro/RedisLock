package com.redislock.lock;

import com.redislock.config.RedisLockProperties;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @Author huaili
 * @Date 2018/11/13 11:00
 * @Description redis的分布式锁
 **/
@Component
public class RedisLock implements InitializingBean {

    @Autowired
    RedisLockProperties redisLockProperties;

    @Autowired
    StringRedisTemplate redisTemplate;

    private static Logger log= Logger.getLogger(RedisLock.class);

    public static final String REDIS_LOCK_POOL_KEY_NAME="redis_distributed_lock_pool";

    public static final String REDIS_LOCK_TIME_OUT="redis_lock_time_out";

    private static final String REDIS_LOCK_KEY_PREV="redis_lock";

    private static final String REDIS_SINGLE_LOCK_KEY="redis_lock0";

    private static long LOCK_TIME_OUT=3000L;


    private ThreadLocal<String> lockedKey=new ThreadLocal();

    private ThreadLocal<String> lockedValue=new ThreadLocal();

    @Override
    public void afterPropertiesSet() throws Exception {

        if(redisLockProperties.getTimeout()!=null&&redisLockProperties.getTimeout()!=0){
            LOCK_TIME_OUT=redisLockProperties.getTimeout().longValue();
        }

        log.debug("redis lock LOCK_TIME_OUT="+LOCK_TIME_OUT);
        log.info("redislock properties="+redisLockProperties);

        log.info("start setting redislock pool size...");
        redisTemplate.opsForValue().set(REDIS_LOCK_POOL_KEY_NAME,redisLockProperties.getSize()+"");
        log.info("set redislock pool size for size=["+redisLockProperties.getSize()+"]");

        log.info("start setting redislock timeout...");
        redisTemplate.opsForValue().set(REDIS_LOCK_TIME_OUT,redisLockProperties.getTimeout()+"");
        log.info("set redislock timeout for timeout=["+redisLockProperties.getTimeout()+"]");
    }

    private enum LockType{
        Single,Mutil;
    }


    /**
     * 单一锁获取
     * @return
     */
    public boolean trylock(){
        return trylock(LockType.Single);
    }

    /**
     * 多个锁获取
     * @return
     */
    public boolean trylocks(){
        return trylock(LockType.Mutil);
    }

    private boolean trylock(LockType type){
        return type== LockType.Single?trySingleLock(REDIS_SINGLE_LOCK_KEY):tryMutilLock();
    }

    /**
     * 单一锁获取
     * @return
     */
    public boolean trySingleLock(String key){

        lockedKey.set(key);
        String curVal=System.currentTimeMillis()+getRedisTimeOut()+"";
        lockedValue.set(curVal);
        try {
            if (redisTemplate.opsForValue().setIfAbsent(key, curVal)) {
                return true;
            } else {
                String oldval = redisTemplate.opsForValue().get(key);

                if (!StringUtils.isEmpty(oldval) && System.currentTimeMillis() > Long.parseLong(oldval) && oldval.equals(redisTemplate.opsForValue().getAndSet(key, curVal))) {
                    return true;
                }
            }
        }catch (Exception e){
            log.error("trySingleLock error"+e.toString()+" key="+key);
        }
        return false;
    }


    /**
     * 获取redis中的超时时间
     * @return
     */
    private long getRedisTimeOut(){
        String var=redisTemplate.opsForValue().get(REDIS_LOCK_TIME_OUT);
        if(!StringUtils.isEmpty(var)){
            long timeout=Long.parseLong(var);
            return timeout>0?timeout:3000;
        }

        return 3000;
    }

    /**
     * 从设定的Redis锁大小中获取
     * @return
     */
    private int getRedisLockCount(){
        String var=redisTemplate.opsForValue().get(REDIS_LOCK_POOL_KEY_NAME);

        if(!StringUtils.isEmpty(var)){
            int size=Integer.parseInt(var);
            return size>0?size:1;
        }
        return 1;

    }

    /**
     * 批量的锁获取提升性能
     * @return
     */
    public boolean tryMutilLock(){

        int locks=getRedisLockCount();

        for(int i=0;i<locks;i++){
            if(trySingleLock(REDIS_LOCK_KEY_PREV+i+"")){
                log.debug("get mutil lock success key="+REDIS_LOCK_KEY_PREV+i+"");
                return true;
            }
        }

        return false;
    }


    /**
     * 解锁
     * @return
     */
    public boolean unlock(){
        try {
            String oldval = redisTemplate.opsForValue().get(lockedKey.get());

            if (!StringUtils.isEmpty(oldval) && oldval.equals(lockedValue.get())) {
                redisTemplate.opsForValue().getOperations().delete(lockedKey.get());
                log.info("unlock success key="+lockedKey.get());
                return true;
            }
            log.error("unlock failed key="+lockedKey.get());

        }catch (Exception e){
            log.error("unlock error"+e.toString());
        }
        return false;

    }
}
