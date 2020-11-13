package com.cus.gf_work.processor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;

/**
 * @author zhaojiejun
 * @date 2020/11/11 11:15 下午
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/start")
    public void test(){
        Spider spider=Spider.create(new JianShuProcessor());
        spider.addUrl("http://www.jianshu.com");
        spider.addPipeline(new NewsPipeline());
        spider.thread(5);
        spider.setExitWhenComplete(true);
        spider.start();
    }
}
