package com.cus.gf_work.service;

/**
 * @author zhaojiejun
 * @date 2020/11/14 10:00 下午
 **/
public interface RedisService {
    /**
     * 校验url是否存在
     * @param key
     * @return
     */
    Boolean isPresent(String key);
}
