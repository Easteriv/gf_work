package com.cus.gf_work.controller;

import com.cus.gf_work.processor.jianshu.JsProcessor;
import com.cus.gf_work.processor.jianshu.JsPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;

/**
 * @author zhaojiejun
 * @date 2020/11/11 11:15 下午
 **/
@RestController
@RequestMapping("/js")
@Slf4j
public class JsController {

    @Autowired
    private JsProcessor jsProcessor;
    @Autowired
    private JsPipeline jsPipeline;

    @RequestMapping("/homePage")
    public void test() {
        Spider spider = Spider.create(jsProcessor);
        spider.addUrl("http://www.jianshu.com");
        spider.addPipeline(jsPipeline);
        spider.thread(10);
        spider.setExitWhenComplete(true);
        spider.start();
    }

    @RequestMapping("/{id}")
    public void test(@PathVariable String id) {
        Spider spider = Spider.create(jsProcessor);
        spider.addUrl("https://www.jianshu.com/p/" + id);
        spider.addPipeline(jsPipeline);
        spider.thread(10);
        spider.setExitWhenComplete(true);
        spider.start();
    }
}
