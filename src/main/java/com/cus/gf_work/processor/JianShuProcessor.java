package com.cus.gf_work.processor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
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
//        List<String> all = page.getHtml().xpath("//article[@class='_2rhmJa']/p").all();
//        StringBuffer stringBuffer = new StringBuffer();
//        for(String str:all){
        //selectable.$("p","text").get()
//            String text = Jsoup.parse(str).text()+"\n"+"\n";
//            stringBuffer.append(text);
//        }
//        page.putField("content",stringBuffer);
//        List<Selectable> nodes = page.getHtml().xpath("//article[@class='_2rhmJa']").nodes();
//        for(Selectable selectable:nodes){
//            System.out.println(selectable);
//            //抽取图片路径
//            //selectable.xpath("//div/img/@data-original-src").get()
//        }
        Selectable selectable = page.getHtml().xpath("//article[@class='_2rhmJa']");
        List<Node> article = Jsoup.parse(selectable.get()).select("article").get(0).childNodes();
        for(Node node:article){
            if(node instanceof TextNode){
                continue;
            }
            else {
                //判断p标签还是img标签
                if("p".equals(((Element) node).tagName())){
                    String text = ((Element) node).text();
                }
                else if("div".equals(((Element) node).tagName())){
                    Elements img = Jsoup.parse(node.toString()).select("img");
                    for(Element element:img){
                        String attr = element.attr("data-original-src");
                        //上传oss
                    }


                }
            }

            System.out.println(node);
            //((Element) node).tagName()
//            if("p".equals(((Element) node).tagName())){
//                String text = ((Element) node).text();
//                System.out.println(text);
//            }

        }


    }

    @Override
    public Site getSite() {
        return site;
    }

}
