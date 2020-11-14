package com.cus.gf_work.service.impl;

import com.cus.gf_work.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author zhaojiejun
 * @date 2020/11/14 10:00 下午
 **/
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Boolean isPresent(String key) {
        Object article = redisTemplate.opsForHash().get("article", key);
        return Objects.nonNull(article)?Boolean.TRUE:Boolean.FALSE;
    }
}
