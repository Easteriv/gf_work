package com.cus.gf_work.controller;

import com.cus.gf_work.pipeline.DbPipeline;
import com.cus.gf_work.processor.huxiu.HxCommonProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;

/**
 * @author zhaojiejun
 * @date 2020/11/17 11:19 下午
 **/
@RestController
@RequestMapping("/hx")
@Slf4j
public class HxController {
    @Autowired
    private HxCommonProcessor hxCommonProcessor;
    @Autowired
    private DbPipeline dbPipeline;


    @RequestMapping("/homePage")
    public void homePage() {
        Spider spider = Spider.create(hxCommonProcessor);
        spider.addUrl("https://www.huxiu.com/");
        spider.addPipeline(dbPipeline);
        spider.thread(10);
        spider.setExitWhenComplete(true);
        spider.start();
    }

    @RequestMapping("/{id}")
    public void customId(@PathVariable String id) {
        Spider spider = Spider.create(hxCommonProcessor);
        spider.addUrl("https://www.huxiu.com/article/"+id+".html");
        spider.addPipeline(dbPipeline);
        spider.thread(10);
        spider.setExitWhenComplete(true);
        spider.start();
    }
}
