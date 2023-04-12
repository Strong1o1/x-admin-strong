package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (Comment)表实体类
 *
 * @author makejava
 * @since 2023-03-21 09:37:57
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
public class Comment {
    //评论ID
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    //动态id
    private Long communicateId;
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
    private Date createTime;


}
