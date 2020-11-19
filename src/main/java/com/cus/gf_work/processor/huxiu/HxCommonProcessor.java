package com.cus.gf_work.processor.huxiu;

import com.aliyun.oss.OSS;
import com.cus.gf_work.common.BrowserConstant;
import com.cus.gf_work.config.OssConfig;
import com.cus.gf_work.service.RedisService;
import com.cus.gf_work.util.Md5Util;
import com.cus.gf_work.util.OssUtil;
import com.cus.gf_work.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhaojiejun
 * @date 2020/11/14 5:08 下午
 **/
@Component
@Slf4j
public class HxCommonProcessor implements PageProcessor {
    @Autowired
    private RedisService redisService;
    @Autowired
    private OSS ossClient;
    @Autowired
    private OssConfig ossConfig;

    private final Site site = Site.me()
            .setDomain(BrowserConstant.HX_DOMAIN)
            //超时时间
            .setTimeOut(10 * 1000)
            //重试时间
            .setRetrySleepTime(3000)
            //重试次数
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setUserAgent(BrowserConstant.USER_AGENT);

    @Override
    public void process(Page page) {
        List<String> all = page.getHtml().xpath("//div[@class='article-item']/a").links().all();
        if (all.size() > 0) {
            //标明这是列表页面
            page.addTargetRequests(all);
            //如果有点击阅读更多，点击按钮
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
            String title = page.getHtml().xpath("//h1[@class='article__title']/text()").get();
            //关键字
            String keyWords = page.getHtml().xpath("//meta[@name='keyWords']//@content").get();
            String content = getContent(page);
            if (StringUtils.isBlank(content)) {
                log.info("文章: {}不含图片,已经跳过采集", url);
            } else {
                page.putField("title", title);
                page.putField("key", key);
                page.putField("originalUrl", url);
                page.putField("content", content);
                //关键字
                page.putField("tkeyc", keyWords);
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
        Selectable selectable = page.getHtml().xpath("//div[@class='article-detail']");
        //判断是否包含图片，文章不包含图片直接不采集
        List<String> imgList = selectable.$("img").all();
        if (CollectionUtils.isEmpty(imgList)) {
            return null;
        }
        Map<String, String> imageUrlMap = new HashMap<>();
        int order = 0;
        StringBuilder stringBuilder = new StringBuilder();
        //如果有头图
        String topImgUrl = selectable.xpath("//div[@class='top-img']/img/@src").get();
        if (StringUtils.isNotBlank(topImgUrl)) {
            //上传
            setTopImg(topImgUrl, imageUrlMap);
        }
        Elements elements = Jsoup.parse(selectable.get()).select(".article__content");
        for (Element element : elements) {
            List<Node> nodeList = element.childNodes();
            for (Node node : nodeList) {
                if (!(node instanceof TextNode)) {
                    //判断p标签还是img标签
                    if ("p".equals(((Element) node).tagName())) {
                        String text = ((Element) node).text();
                        if (StringUtils.isBlank(text) && ((Element) node).select("br").size() > 0) {
                            continue;
                        }
                        //加粗标签
                        if (((Element) node).select("strong").size() > 0 || "text-big-title".equals(((Element) node)
                                .attr("class"))) {
                            text = "**" + text + "**";
                        }
                        //图片解释
                        if ("text-img-note".equals(((Element) node).attr("class"))) {
                            continue;
                        }
                        //去除版权来源
                        if (((Element) node).select(".text-remarks").size() > 0 && text.startsWith("本文来自")) {
                            continue;
                        }
                        //p标签下面的 img 标签
                        if (((Element) node).select("img").size() > 0) {
                            String attr = ((Element) node).select("img").attr("_src");
                            order += 1;
                            setImg(attr, imageUrlMap, order, stringBuilder, "请输入图片描述");
                        }
                        stringBuilder.append(text).append("\n").append("\n");
                    }
                }
            }
        }
        page.putField("bigImgUrl", imageUrlMap.get("imgUrl"));
        return stringBuilder.toString();
    }

    /**
     * 根据原来的图片url重新格式化成typecho需要的url格式
     *
     * @param imgUrl         源url
     * @param imageUrlMap    map容器
     * @param order          标明图片张数
     * @param stringBuilder  拼接容器
     * @param imgDescription 图片描述
     */
    private void setImg(String imgUrl, Map<String, String> imageUrlMap, Integer order, StringBuilder stringBuilder, String imgDescription) {
        Optional<String> realUrl = UrlUtil.getRealUrl(imgUrl);
        if (realUrl.isPresent()) {
            String url = realUrl.get();
            String filename = StringUtils.substringAfterLast(url, "/");
            String objectName = "uPic/" + filename;
            //上传oss
            String uploadUrl = OssUtil.upload(url, objectName);
            if (StringUtils.isNotBlank(uploadUrl)) {
                imageUrlMap.putIfAbsent("imgUrl", uploadUrl);
                //拼接
                String des = "![" + imgDescription + "][" + order + "]";
                stringBuilder.append(des).append("\n").append("\n");
                // [1]: https://appleid.apple.com/
                String imgLink = "[" + order + "]: " + uploadUrl;
                stringBuilder.append(imgLink).append("\n").append("\n");
            }
        }
    }

    /**
     * 上传头图
     *
     * @param imgUrl      图片url
     * @param imageUrlMap 图片容器
     */
    private void setTopImg(String imgUrl, Map<String, String> imageUrlMap) {
        Optional<String> realUrl = UrlUtil.getRealUrl(imgUrl);
        if (realUrl.isPresent()) {
            String url = realUrl.get();
            String filename = StringUtils.substringAfterLast(url, "/");
            String objectName = "uPic/" + filename;
            //上传oss
            String uploadUrl = OssUtil.upload(url, objectName);
            if (StringUtils.isNotBlank(uploadUrl)) {
                imageUrlMap.putIfAbsent("imgUrl", uploadUrl);
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
