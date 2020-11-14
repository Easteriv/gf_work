package com.cus.gf_work.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author zhaojiejun
 * @date 2020/11/14 5:26 下午
 **/
public class UrlUtil {
    public static Optional<String> getRealUrl(String imgUrl) {
        if (StringUtils.isNotBlank(imgUrl)) {
            String subUrl = StringUtils.substringBeforeLast(imgUrl, "?");
            if (imgUrl.startsWith("http") || imgUrl.startsWith("https")) {
                return Optional.of(subUrl);
            } else {
                String format = String.format("https:%s", subUrl);
                return Optional.of(format);
            }
        } else {
            return Optional.empty();
        }
    }
}
