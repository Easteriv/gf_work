package com.cus.gf_work.processor;

import org.springframework.stereotype.Service;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Map;

/**
 * @author zhaojiejun
 * @date 2020/11/11 11:09 下午
 **/
@Service
public class NewsPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            if (entry.getKey().contains("news")) {
                News news=(News) entry.getValue();

                System.out.println(news);
            }

        }
    }
}
