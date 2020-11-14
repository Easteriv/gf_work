package com.cus.gf_work.service.impl;

import com.cus.gf_work.dao.Metas;
import com.cus.gf_work.dao.PostContent;
import com.cus.gf_work.dao.Relationship;
import com.cus.gf_work.mapper.MetasMapper;
import com.cus.gf_work.mapper.PostContentMapper;
import com.cus.gf_work.mapper.RelationshipMapper;
import com.cus.gf_work.service.PostContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhaojiejun
 * @date 2020/11/14 7:11 下午
 **/
@Service
public class PostContentServiceImpl implements PostContentService {
    @Autowired
    private PostContentMapper postContentMapper;
    @Autowired
    private RelationshipMapper relationshipMapper;
    @Autowired
    private MetasMapper metasMapper;

    /**
     * 将文章插入数据库
     *
     * @param postContent 文章实体类
     * @return 成功 :true 失败：false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPostFacade(PostContent postContent) {
        //插入文章
        postContentMapper.insert(postContent);
        Integer cid = postContent.getCid();
        postContent.setSlug(String.valueOf(cid));
        postContentMapper.updateById(postContent);
        //插入对应关系 relationShip表
        Relationship relationship = new Relationship();
        relationship.setCid(cid);
        relationship.setMid(2);
        relationshipMapper.insert(relationship);
        //插入自增数量
        Metas metas = metasMapper.selectById(2);
        Integer count = metas.getCount();
        metas.setCount(++count);
        metasMapper.updateById(metas);
        return true;
    }
}
