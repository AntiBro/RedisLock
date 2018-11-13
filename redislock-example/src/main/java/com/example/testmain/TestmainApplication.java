package com.example.testmain;

import com.redislock.config.RedisLockConfigure;
import com.redislock.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TestmainApplication {
    private static Logger log= Logger.getLogger(RedisLockConfigure.class);


    @Autowired
    RedisLock lock;

    public static void main(String[] args) {
        SpringApplication.run(TestmainApplication.class, args);
    }

    @RequestMapping("/test")
    public String test(){

        for(int i=0;i<3;i++){
            new Thread(new TestThread(i,lock)).start();
        }

        return "21323";
    }
}
