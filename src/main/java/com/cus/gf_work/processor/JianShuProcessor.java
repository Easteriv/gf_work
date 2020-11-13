package com.cus.gf_work.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * @author zhaojiejun
 * @date 2020/11/11 11:03 下午
 **/
public class JianShuProcessor implements PageProcessor {
    private final Site site = Site.me()
            .setDomain("jianshu.com")
            .setSleepTime(100)
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
    ;

    public static final String LIST = "http://www.jianshu.com";

    @Override
    public void process(Page page) {
        List<String> all = page.getHtml().css(".title").links().all();
        if (all.size() > 0) {
            page.addTargetRequests(all);
        } else {
            handle(page);
        }


    }

    private void handle(Page page) {
        String s = page.getHtml().xpath("//article[@class='_2rhmJa']").get();
        System.out.println(s);
    }

    @Override
    public Site getSite() {
        return site;
    }

}
