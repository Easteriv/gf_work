package com.cus.gf_work.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaojiejun
 * @date 2020/11/14 5:43 下午
 **/
@Data
@ConfigurationProperties(prefix = "oss")
@Configuration
public class OssConfig {
    /**
     * 阿里云 oss 站点
     */
    private String endpoint;

    /**
     * 阿里云 oss 资源访问 url
     */
    private String url;

    /**
     * 阿里云 oss 公钥
     */
    private String accessKeyId;

    /**
     * 阿里云 oss 私钥
     */
    private String accessKeySecret;

    /**
     * 阿里云 oss bucket
     */
    private String bucketName;
}
