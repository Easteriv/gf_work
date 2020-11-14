package com.cus.gf_work.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author zhaojiejun
 * @date 2020/11/14 5:47 下午
 **/
@Component
public class CommonComponent {
    @Autowired
    private OssConfig ossConfig;
    @Bean
    public OSS ossClient(){
        return new OSSClientBuilder().build(ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
    }
}
