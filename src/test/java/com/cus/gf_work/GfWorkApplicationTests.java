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
        // https://www.jianshu.com/p/3f53cc255c3a
        redisTemplate.opsForHash().delete("article","19c6078d5b06c18d");
        Object article = redisTemplate.opsForHash().get("article", "19c6078d5b06c18d");
        System.out.println(article);
    }

}
