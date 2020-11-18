package com.cus.gf_work.pipeline;

import com.cus.gf_work.dao.PostContent;
import com.cus.gf_work.service.PostContentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * @author zhaojiejun
 * @date 2020/11/14 12:57 下午
 **/
@Service
@Slf4j
public class DbPipeline implements Pipeline {
    @Autowired
    private PostContentService postContentService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void process(ResultItems resultItems, Task task) {
        Object content = resultItems.get("content");
        Object title = resultItems.get("title");
        Object key = resultItems.get("key");
        Object originalUrl = resultItems.get("originalUrl");
        Object tkeyc = resultItems.get("tkeyc");
        if (content != null && title != null && key != null) {
            String contentStr = content.toString();
            String titleStr = title.toString();
            String keyStr = key.toString();
            String originalUrlStr = originalUrl.toString();
            Object imgObj = resultItems.get("bigImgUrl");
            String bigImgUrl = String.valueOf(imgObj);
            String keyWords = String.valueOf(tkeyc);
            contentStr = "<!--markdown-->" + contentStr;
            //插入数据库
            PostContent postContent = buildPostContent(titleStr, contentStr,bigImgUrl,keyWords);
            Boolean isSuccess = postContentService.insertPostFacade(postContent);
            if (isSuccess) {
                log.info("[{}]插入数据库成功,原文链接:{}", titleStr,originalUrlStr);
                Boolean isPutSuccess = redisTemplate.opsForHash().putIfAbsent("article", keyStr, "0");
                if (isPutSuccess) {
                    log.info("[{}]插入redis成功,key is:{}", titleStr, keyStr);
                } else {
                    log.error("[{}]插入redis失败,key is:{}", titleStr, keyStr);
                }
            }
        }
    }

    private PostContent buildPostContent(String title, String content,String bigImgUrl,String keyWords) {
        Long time = System.currentTimeMillis() / 1000L;
        double d = Math.random();
        int views = 100 + (int) (d * 100);
        int agree = (int) (d * 10);
        return PostContent.builder().title(title).created(time)
                .modified(time).text(content).order(NumberUtils.INTEGER_ZERO)
                .authorId(1).type("post").status("publish").commentsNum(NumberUtils.INTEGER_ZERO)
                .allowComment("1").allowPing("1").allowFeed("1").parent(NumberUtils.INTEGER_ZERO)
                .views(views).agree(agree).bigImage(bigImgUrl).
                        keyWords(keyWords).
                        build();
    }
}
