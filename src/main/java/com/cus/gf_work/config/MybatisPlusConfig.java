package com.cus.gf_work.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaojiejun
 * @version V1.0
 * @date 2020/6/6 2:58 下午
 * @description MP配置文件
 **/
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页拦截器
     * @return PaginationInterceptor
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
