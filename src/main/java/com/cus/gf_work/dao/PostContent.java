package com.cus.gf_work.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章内容表
 *
 * @author zhaojiejun
 * @date 2020/11/7 10:24 下午
 **/
@Data
@TableName("typecho_contents")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostContent {
    /**
     * 文章id
     */
    @TableId(value = "cid", type = IdType.AUTO)
    private Integer cid;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 内容缩略名
     */
    private String slug;
    /**
     * 内容生成时的GMT unix时间戳
     */
    private Long created;
    /**
     * 内容更改时的GMT unix时间戳
     */
    private Long modified;
    /**
     * 内容文字
     */
    private String text;
    /**
     * 排序
     */
    @TableField("`order`")
    private Integer order;
    /**
     * 内容所属用户id
     */
    @TableField(value = "authorId")
    private Integer authorId;
    /**
     * 内容使用的模板
     */
    private String template;
    /**
     * 内容类别
     */
    private String type;
    /**
     * 内容状态
     */
    private String status;
    /**
     * 受保护内容,此字段对应内容保护密码
     */
    private String password;
    /**
     * 内容所属评论数,冗余字段
     */
    @TableField(value = "commentsNum")
    private Integer commentsNum;
    /**
     * 是否允许评论
     */
    @TableField(value = "allowComment")
    private String allowComment;
    /**
     * 是否允许ping
     */
    @TableField(value = "allowPing")
    private String allowPing;
    /**
     * 允许出现在聚合中
     */
    @TableField(value = "allowFeed")
    private String allowFeed;
    /**
     * 未知
     */
    private Integer parent;
    /**
     * 点赞
     */
    private Integer agree;
    /**
     * 观看次数
     */
    private Integer views;


    @TableField(exist = false)
    private String bigImage;
    @TableField(exist = false)
    private String keyWords;
}
