package com.cus.gf_work.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 项目表
 * @author zhaojiejun
 * @date 2020/11/7 10:47 下午
 **/
@Data
@TableName("typecho_metas")
public class Metas {
    /**
     * 项目主键
     */
    @TableId(value = "mid",type = IdType.AUTO)
    private Integer mid;
    /**
     * 名称
     */
    private String name;
    /**
     * 项目缩略名
     */
    private String slug;
    /**
     * 项目类型
     */
    private String type;
    /**
     * 选项描述
     */
    private String description;
    /**
     * 项目所属内容个数
     */
    private Integer count;
    /**
     * 	项目排序
     */
    @TableField("`order`")
    private Integer order;
    /**
     * 父类目Id
     */
    private Integer parent;
}
