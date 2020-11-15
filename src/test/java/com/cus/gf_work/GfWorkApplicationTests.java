package com.cus.gf_work;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class GfWorkApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        // https://www.jianshu.com/p/0d242366204b
        redisTemplate.opsForHash().delete("article","730c0aee8905b6f4");
        Object article = redisTemplate.opsForHash().get("article", "730c0aee8905b6f4");
        System.out.println(article);
    }

}
