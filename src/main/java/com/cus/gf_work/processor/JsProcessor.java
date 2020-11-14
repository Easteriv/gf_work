package com.cus.gf_work.processor;

import com.aliyun.oss.OSS;
import com.cus.gf_work.common.BrowserConstant;
import com.cus.gf_work.config.OssConfig;
import com.cus.gf_work.service.RedisService;
import com.cus.gf_work.util.Md5Util;
import com.cus.gf_work.util.OssUtil;
import com.cus.gf_work.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.Optional;

/**
 * @author zhaojiejun
 * @date 2020/11/14 5:08 下午
 **/
@Component
@Slf4j
public class JsProcessor implements PageProcessor {
    @Autowired
    private RedisService redisService;
    @Autowired
    private OSS ossClient;
    @Autowired
    private OssConfig ossConfig;
    private final Site site = Site.me()
            .setDomain(BrowserConstant.JS_DOMAIN)
            .setSleepTime(100)
            .setUserAgent(BrowserConstant.USER_AGENT);

    @Override
    public void process(Page page) {
        List<String> all = page.getHtml().css(".title").links().all();
        if (all.size() > 0) {
            page.addTargetRequests(all);
        } else {
            handle(page);
        }
    }

    /**
     * 处理详情页面
     *
     * @param page page参数
     */
    private void handle(Page page) {
        //网页去重
        String url = page.getUrl().get();
        String key = Md5Util.str2Md5(url);
        Boolean present = redisService.isPresent(key);
        if (!present) {
            //标题
            String title = page.getHtml().$("h1._1RuRku", "text").get();
            String content = getContent(page);
            if (StringUtils.isBlank(content)) {
                log.info("文章: {}不含图片,已经跳过采集",url);
            } else {
                page.putField("title", title);
                page.putField("key", key);
                page.putField("content", content);
            }
        } else {
            log.info("网页:{}重复，已跳过采集", url);
        }
    }

    /**
     * 获取文章内容
     *
     * @param page page参数
     * @return 返回文章内容
     */
    private String getContent(Page page) {
        Selectable selectable = page.getHtml().xpath("//article[@class='_2rhmJa']");
        //判断是否包含图片，文章不包含图片直接不采集
        String imgTag = selectable.$("img").get();
        if(StringUtils.isBlank(imgTag)){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Elements elements = Jsoup.parse(selectable.get()).select("article");
        for (Element element : elements) {
            List<Node> nodeList = element.childNodes();
            int order = 0;
            for (Node node : nodeList) {
                if (!(node instanceof TextNode)) {
                    //判断p标签还是img标签
                    if ("p".equals(((Element) node).tagName())) {
                        String text = ((Element) node).text() + "\n" + "\n";
                        stringBuilder.append(text);
                    } else if ("div".equals(((Element) node).tagName())) {
                        String imgDescription = Jsoup.parse(node.toString()).text();
                        if (StringUtils.isBlank(imgDescription)) {
                            imgDescription = "请输入图片描述";
                        }
                        Elements img = Jsoup.parse(node.toString()).select("img");
                        for (Element imgElement : img) {
                            //图片
                            String imgUrl = imgElement.attr("data-original-src");
                            Optional<String> realUrl = UrlUtil.getRealUrl(imgUrl);
                            if (realUrl.isPresent()) {
                                String url = realUrl.get();
                                String filename = StringUtils.substringAfterLast(url, "/");
                                String objectName = "uPic/" + filename;
                                //上传oss
                                String uploadUrl = OssUtil.upload(url, objectName);
                                if (StringUtils.isNotBlank(uploadUrl)) {
                                    order++;
                                    //拼接
                                    String des = "![" + imgDescription + "][" + order + "]";
                                    stringBuilder.append(des).append("\n").append("\n");
                                    // [1]: https://appleid.apple.com/
                                    String imgLink = "[" + order + "]: " + uploadUrl;
                                    stringBuilder.append(imgLink).append("\n").append("\n");
                                }
                            }
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public Site getSite() {
        return site;
    }
}
