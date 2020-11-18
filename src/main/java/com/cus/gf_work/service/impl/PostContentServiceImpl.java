package com.cus.gf_work.service.impl;

import com.cus.gf_work.dao.Fields;
import com.cus.gf_work.dao.Metas;
import com.cus.gf_work.dao.PostContent;
import com.cus.gf_work.dao.Relationship;
import com.cus.gf_work.mapper.FieldsMapper;
import com.cus.gf_work.mapper.MetasMapper;
import com.cus.gf_work.mapper.PostContentMapper;
import com.cus.gf_work.mapper.RelationshipMapper;
import com.cus.gf_work.service.FieldService;
import com.cus.gf_work.service.PostContentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
    @Autowired
    private FieldService fieldService;
    @Autowired
    private FieldsMapper fieldsMapper;

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
        //插入fields表
        Map<String, String> stringStringMap = fieldService.selectMap(cid);
        String bigImage = postContent.getBigImage();
        if (StringUtils.isNotBlank(bigImage)) {
            if (stringStringMap.get("img") == null) {
                Fields fields = new Fields();
                fields.setCid(cid);
                fields.setName("img");
                fields.setType("str");
                fields.setStrValue(bigImage);
                fieldsMapper.insert(fields);
            }
            if (stringStringMap.get("bimg") == null) {
                Fields fields = new Fields();
                fields.setCid(cid);
                fields.setName("bimg");
                fields.setType("str");
                fields.setStrValue(bigImage);
                fieldsMapper.insert(fields);
            }
        }
        if (stringStringMap.get("tktit") == null) {
            Fields fields = new Fields();
            fields.setCid(cid);
            fields.setName("tktit");
            fields.setType("str");
            fields.setStrValue(postContent.getTitle());
            fieldsMapper.insert(fields);
        }
        if (stringStringMap.get("tkeyc") == null) {
            Fields fields = new Fields();
            fields.setCid(cid);
            fields.setName("tkeyc");
            fields.setType("str");
            fields.setStrValue(postContent.getKeyWords());
            fieldsMapper.insert(fields);
        }
        return true;
    }
}
