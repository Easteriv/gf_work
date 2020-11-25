package com.cus.gf_work.processor.guoyans;

import com.aliyun.oss.OSS;
import com.cus.gf_work.common.BrowserConstant;
import com.cus.gf_work.config.OssConfig;
import com.cus.gf_work.service.RedisService;
import com.cus.gf_work.util.Md5Util;
import com.cus.gf_work.util.OssUtil;
import com.cus.gf_work.util.TimeUtil;
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
public class GysCommonProcessor implements PageProcessor {
    @Autowired
    private RedisService redisService;
    @Autowired
    private OSS ossClient;
    @Autowired
    private OssConfig ossConfig;
    private static Integer START_PAGE = 1;
    private static boolean FLAG = true;
    private final Site site = Site.me()
            .setDomain(BrowserConstant.GYS_DOMAIN)
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
        List<String> all = page.getHtml().xpath("//span[@class='entry-title']").links().all();
        if (all.size() > 0 && FLAG) {
            //标明这是列表页面
            page.addTargetRequests(all);
            log.info("开始抓取第:{}页数据", START_PAGE);
            START_PAGE += 1;
            page.addTargetRequest("https://www.guoyanys.com/film/" + START_PAGE + "/");
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
            //距离超过2天的不需要时间 2020-11-19T14:52:36+08:00
            String time = page.getHtml().xpath("//time//@datetime").get();
            String timeStr = StringUtils.substringBeforeLast(time, "T");
            Boolean isBefore = TimeUtil.isTrue(timeStr, 1L);
            if (!isBefore) {
                FLAG = false;
                log.info("时间:{}超过设定的时间,故不采集", timeStr);
            }
            //标题
            String title = page.getHtml().xpath("//h1[@class='entry-title page-title']/text()").get();
            String content = getContent(page);
            if (StringUtils.isBlank(content)) {
                log.info("文章: {}不含图片,已经跳过采集", url);
            } else {
                page.putField("title", title);
                page.putField("key", key);
                page.putField("originalUrl", url);
                page.putField("content", content);
                page.putField("mid", 3);
                page.putField("tkeyc", "最新电影推荐");
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
        Selectable selectable = page.getHtml().xpath("//div[@class='show_text']");
        //判断是否包含图片，文章不包含图片直接不采集
        List<String> imgList = selectable.$("img").all();
        if (CollectionUtils.isEmpty(imgList)) {
            return null;
        }
        Map<String, String> imageUrlMap = new HashMap<>();
        int order = 0;
        StringBuilder stringBuilder = new StringBuilder();
        Elements elements = Jsoup.parse(selectable.get()).select("p");
        for (Element element : elements) {
            List<Node> nodeList = element.childNodes();
            for (Node node : nodeList) {
                if (node instanceof TextNode) {
                    String text = ((TextNode) node).text();
                    //长度小于20个字，加粗
                    if(text.length()<=20){
                        text = "**" + text + "**";
                    }
                    stringBuilder.append(text).append("\n").append("\n");
                    continue;
                }
                if ("br".equals(((Element) node).tagName())) {
                    stringBuilder.append("\n").append("\n");
                    continue;
                }
                if (((Element) node).select("img").size() > 0) {
                    String attr = ((Element) node).select("img").attr("src");
                    order += 1;
                    setImg(attr, imageUrlMap, order, stringBuilder, "请输入图片描述");
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
