package com.cus.gf_work.service;


import com.cus.gf_work.dao.PostContent;

/**
 * 插入文章
 * @author zhaojiejun
 */
public interface PostContentService {
    /**
     * 将文章插入数据库
     * @param postContent 文章实体类
     * @return 成功 :true 失败：false
     */
    public Boolean insertPostFacade(PostContent postContent);
}
