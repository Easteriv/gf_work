package com.cus.gf_work.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 关系表
 * @author zhaojiejun
 * @date 2020/11/7 10:50 下午
 **/
@Data
@TableName("typecho_relationships")
public class Relationship {
    /**
     * 内容主键
     */
    private Integer cid;
    /**
     * 项目主键
     */
    private Integer mid;
}
