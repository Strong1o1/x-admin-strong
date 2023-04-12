package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
@TableName("t_message")
public class Message extends Model<Message> {
    /**
     * ID主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //根评论id
    private Long rootId;
    //内容
    private String content;
    //所回复评论的评论人id
    private Long toCommentUserId;
    //所回复的评论id
    private Long toCommentId;
    //评论人id
    private Long createBy;
    //评论时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date createTime;


}
