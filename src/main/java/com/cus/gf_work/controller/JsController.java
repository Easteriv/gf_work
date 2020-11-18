package com.cus.gf_work.controller;

import com.cus.gf_work.pipeline.DbPipeline;
import com.cus.gf_work.processor.jianshu.JsCommonProcessor;
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
    private JsCommonProcessor jsCommonProcessor;
    @Autowired
    private DbPipeline dbPipeline;

    /**
     * 从主页抓取
     */
    @RequestMapping("/homePage")
    public void homePage() {
        Spider spider = Spider.create(jsCommonProcessor);
        spider.addUrl("http://www.jianshu.com");
        spider.addPipeline(dbPipeline);
        spider.thread(10);
        spider.setExitWhenComplete(true);
        spider.start();
    }

    /**
     * 自定义抓取文章页面
     *
     * @param id 简书文章唯一标识
     */
    @RequestMapping("/{id}")
    public void customId(@PathVariable String id) {
        Spider spider = Spider.create(jsCommonProcessor);
        spider.addUrl("https://www.jianshu.com/p/" + id);
        spider.addPipeline(dbPipeline);
        spider.thread(10);
        spider.setExitWhenComplete(true);
        spider.start();
    }

    /**
     * 简书热门文章抓取
     */
    @RequestMapping("/hot")
    public void hot() {

//        spider.addPipeline(jsPipeline);
//        spider.thread(10);
//        spider.setExitWhenComplete(true);
//        spider.start();
    }
}
