package com.cus.gf_work;

import com.cus.gf_work.util.Md5Util;
import com.cus.gf_work.util.UrlUtil;
import org.junit.jupiter.api.Test;

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
}
