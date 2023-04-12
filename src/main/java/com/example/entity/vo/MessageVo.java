package com.example.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageVo {

    //评论id
    private Long id;
    //评论人的头像
    private String avatar;
    //根评论id
    private Long rootId;
    //内容
    private String content;
    //所回复评论的评论人昵称
    private String toCommentUserName;
    //所回复的评论id
    private Long toCommentId;
    //评论时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;
    //评论人id
    private Long userId;
    //评论人昵称
    private String username;

    private List<MessageVo> children;
}
