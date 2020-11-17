package com.cus.gf_work.processor.jianshu;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 简书热门文章抓取
 * @author zhaojiejun
 * @date 2020/11/15 4:52 下午
 **/
@Component
public class JsHotProcessor implements PageProcessor {
    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return null;
    }
}
