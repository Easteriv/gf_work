package com.cus.gf_work.controller.crawler;

import com.cus.gf_work.pipeline.DbPipeline;
import com.cus.gf_work.processor.guoyans.GysCommonProcessor;
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
@RequestMapping("/gys")
@Slf4j
public class GysController {
    @Autowired
    private GysCommonProcessor gysCommonProcessor;
    @Autowired
    private DbPipeline dbPipeline;


    @RequestMapping("/homePage")
    public void homePage() {
        Spider spider = Spider.create(gysCommonProcessor);
        spider.addUrl("https://www.guoyanys.com/film/1/");
        spider.addPipeline(dbPipeline);
        spider.thread(10);
        spider.setExitWhenComplete(true);
        spider.start();
    }

    @RequestMapping("/{id}")
    public void customId(@PathVariable String id) {
        Spider spider = Spider.create(gysCommonProcessor);
        spider.addUrl("https://www.guoyanys.com/" + id + ".html");
        spider.addPipeline(dbPipeline);
        spider.thread(10);
        spider.setExitWhenComplete(true);
        spider.start();
    }

}
