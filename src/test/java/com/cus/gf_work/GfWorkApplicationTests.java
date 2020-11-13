package com.cus.gf_work;

import com.cus.gf_work.processor.JianShuProcessor;
import com.cus.gf_work.processor.NewsPipeline;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import us.codecraft.webmagic.Spider;

@SpringBootTest
class GfWorkApplicationTests {

    @Test
    void contextLoads() {
        Spider spider=Spider.create(new JianShuProcessor());
        spider.addUrl("http://www.jianshu.com");
        spider.addPipeline(new NewsPipeline());
        spider.thread(5);
        spider.setExitWhenComplete(true);
        spider.start();
    }

}
