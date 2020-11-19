package com.cus.gf_work.processor.jianshu;

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
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.*;

/**
 * @author zhaojiejun
 * @date 2020/11/14 5:08 下午
 **/
@Component
@Slf4j
public class JsCommonProcessor implements PageProcessor {
    @Autowired
    private RedisService redisService;
    @Autowired
    private OSS ossClient;
    @Autowired
    private OssConfig ossConfig;

    private static Integer START_PAGE = 1;
    public static List<String> IDS_LIST = new ArrayList<>();
    private static final Integer MAX_GET_PAGE = 3;
    private final Site site = Site.me()
            .addHeader("Cookie","_ga=GA1.2.1774951478.1591365670; __yadk_uid=NBlKFuNce4hLeX1bfAgIBrNmwBHNMTfz; __gads=ID=9be5071f9c703d1b:T=1591365681:S=ALNI_MbsuMoX_vM1xNI25z8X67KxayYUzg; read_mode=day; default_font=font2; locale=zh-CN; Hm_lvt_0c0e9d9b1e7d617b3e6842e85b9fb068=1605429310,1605429602,1605447339,1605622554; remember_user_token=W1syNTE5NjQ1N10sIiQyYSQxMSR5ajhJMUYxQ1RBUE0wNEZ2N1hWTlpPIiwiMTYwNTYyMjY5OS43NjIzMjkzIl0%3D--a962f13c8e58e7e757699622c141c78d202e7347; web_login_version=MTYwNTYyMjY5OQ%3D%3D--0d4626d67c0c27a7d8468a440d918f784e55f905; _m7e_session_core=ce86b319c58f93268bc1e60e8fa7bb06; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2225196457%22%2C%22first_id%22%3A%2217284c914fe919-03b398592ed8d1-143e6257-1296000-17284c914ffa42%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_utm_source%22%3A%22desktop%22%2C%22%24latest_utm_medium%22%3A%22index-users%22%2C%22%24latest_referrer_host%22%3A%22%22%7D%2C%22%24device_id%22%3A%2217284c914fe919-03b398592ed8d1-143e6257-1296000-17284c914ffa42%22%7D; Hm_lpvt_0c0e9d9b1e7d617b3e6842e85b9fb068=1605622701")
            .setDomain(BrowserConstant.JS_DOMAIN)
            .addHeader("X-PJAX","true")
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
        List<String> all = page.getHtml().css(".title").links().all();
        if (all.size() > 0) {
            //标明这是列表页面
            page.addTargetRequests(all);
            List<String> ids = page.getHtml().xpath("//li[@data-note-id]//@data-note-id").all();
            IDS_LIST.addAll(ids);
            //判断是否有阅读更多按钮
            //String readMore = page.getHtml().css(".load-more").get();
            //if (StringUtils.isBlank(readMore)) {
            //添加url
            if (START_PAGE < MAX_GET_PAGE) {
                START_PAGE += 1;
                String nextUrl = "https://www.jianshu.com/?" + UrlUtil.buildUrl(IDS_LIST) + "&page=" + START_PAGE;
                page.addTargetRequest(nextUrl);
                log.info("抓取第:{}页数据", START_PAGE);
                //}
            } else if(START_PAGE<6){
                START_PAGE += 1;
                //模拟post请求
                Request req = new Request();
                req.setMethod(HttpConstant.Method.POST);
                req.setUrl("https://www.jianshu.com/trending_notes");
                //设置MAP参数
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("page", START_PAGE);
                paramMap.put("X-PJAX",true);
                req.setRequestBody(HttpRequestBody.form(paramMap, "UTF-8"));
                page.addTargetRequest(req);
                log.info("抓取第:{}页数据", START_PAGE);
            }
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
                log.info("文章: {}不含图片,已经跳过采集", url);
            } else {
                page.putField("title", title);
                page.putField("key", key);
                page.putField("originalUrl", url);
                page.putField("content", content);
                page.putField("mid",2);
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
        if (StringUtils.isBlank(imgTag)) {
            return null;
        }
        Map<String, String> imageUrlMap = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        Elements elements = Jsoup.parse(selectable.get()).select("article");
        for (Element element : elements) {
            List<Node> nodeList = element.childNodes();
            int order = 0;
            for (Node node : nodeList) {
                if (!(node instanceof TextNode)) {
                    //判断p标签还是img标签
                    if ("p".equals(((Element) node).tagName())) {
                        String text = ((Element) node).text();
                        //b标签
                        if (((Element) node).select("b").size() > 0) {
                            text = "**" + text + "**";
                        }
                        //p标签下面的 img 标签
                        if (((Element) node).select("img").size() > 0) {
                            String attr = ((Element) node).select("img").attr("src");
                            order += 1;
                            setImg(attr, imageUrlMap, order, stringBuilder, "请输入图片描述");
                        }
                        stringBuilder.append(text).append("\n").append("\n");
                    } else if ("div".equals(((Element) node).tagName())) {
                        String imgDescription = Jsoup.parse(node.toString()).text();
                        if (StringUtils.isBlank(imgDescription)) {
                            imgDescription = "请输入图片描述";
                        }
                        Elements img = Jsoup.parse(node.toString()).select("img");
                        for (Element imgElement : img) {
                            //图片
                            String imgUrl = imgElement.attr("data-original-src");
                            order += 1;
                            setImg(imgUrl, imageUrlMap, order, stringBuilder, imgDescription);
                        }
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

    @Override
    public Site getSite() {
        return site;
    }
}
