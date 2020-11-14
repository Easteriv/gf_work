package com.cus.gf_work.util;

import com.aliyun.oss.OSS;
import com.cus.gf_work.config.OssConfig;
import com.cus.gf_work.service.SpringContextBeanService;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URL;

/**
 * @author zhaojiejun
 * @date 2020/11/14 5:57 下午
 **/
@Slf4j
public class OssUtil {
    public static String upload(String url, String objectName) {
        InputStream inputStream;
        try {
            inputStream = new URL(url).openStream();
            OSS ossClient = SpringContextBeanService.getBean(OSS.class);
            OssConfig ossConfig = SpringContextBeanService.getBean(OssConfig.class);
            ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream);
            return String.format("https://cdn.suscrb.com/%s", objectName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
