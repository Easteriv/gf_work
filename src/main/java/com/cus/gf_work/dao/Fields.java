package com.cus.gf_work.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zhaojiejun
 * @date 2020/11/8 4:48 下午
 **/
@Data
@TableName("typecho_fields")
public class Fields {
    /**
     * 文章ID
     */
    private Integer cid;
    /**
     * 字段名称
     */
    private String name;
    /**
     * 字段类型
     */
    private String type;
    /**
     * 字段值
     */
    private String strValue;
}
