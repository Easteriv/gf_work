package com.cus.gf_work.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cus.gf_work.dao.Fields;
import com.cus.gf_work.mapper.FieldsMapper;
import com.cus.gf_work.service.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhaojiejun
 * @date 2020/11/8 6:16 下午
 **/
@Service
public class FieldServiceImpl implements FieldService {
    @Autowired
    private FieldsMapper fieldsMapper;

    /**
     * 根据文章id查找文章配置参数信息
     * @param cid 文章主键ID
     * @return 文章配置参数
     */
    @Override
    public Map<String,String> selectMap(Integer cid) {
        List<Fields> fields = fieldsMapper.selectList(new QueryWrapper<Fields>().eq("cid", cid));
        return fields.stream().collect(Collectors.toMap(Fields::getName, Fields::getStrValue, (key1, key2) -> key2));
    }
}
