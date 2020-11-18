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
        redisTemplate.opsForHash().delete("article","53d1bbe398b36e38");
        Object article = redisTemplate.opsForHash().get("article", "53d1bbe398b36e38");
        System.out.println(article);
    }

}
