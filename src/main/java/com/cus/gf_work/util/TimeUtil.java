package com.cus.gf_work.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author zhaojiejun
 * @date 2020/11/19 10:01 下午
 **/
public class TimeUtil {
    public static Boolean isTrue(String date, Long between) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parse = LocalDate.parse(date, dateTimeFormatter);
        return LocalDate.now().toEpochDay() - parse.toEpochDay() <= between;
    }
}
