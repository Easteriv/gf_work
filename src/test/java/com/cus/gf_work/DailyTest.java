package com.cus.gf_work;

import com.cus.gf_work.util.Md5Util;
import com.cus.gf_work.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaojiejun
 * @date 2020/11/14 10:05 下午
 **/
public class DailyTest {
    @Test
    public void testMd5(){
        String sunzn = Md5Util.str2Md5("http://www.jianshu.com/p/2f0b4659e598");
        System.out.println(sunzn);
    }

    @Test
    public void testUrl(){
        List<String> ids=new ArrayList<String>();
        ids.add("1232");
        ids.add("1233");
        ids.add("1234");
        ids.add("1235");
        String s = UrlUtil.buildUrl(ids);
        System.out.println(s);
    }
    @Test
    public void testTime(){
        String time = "2020-11-19T14:52:36+08:00";
        String timeStr = StringUtils.substringBeforeLast(time, "T");
        System.out.println(timeStr);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parse = LocalDate.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("相差"+(LocalDate.now().toEpochDay()-parse.toEpochDay()));
    }
}
