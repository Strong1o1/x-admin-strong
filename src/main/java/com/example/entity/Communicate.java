package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("communicate")
public class Communicate extends Model<Communicate> {
    /**
      * 动态ID主键
      */
@TableId(value = "communicate_id", type = IdType.AUTO)
    private Long communicateId;

    /**
      * 内容
      */
    private String content;

    /**
      * 图片
      */
    private String picture;

    /**
      * 点赞数
      */
    private Integer giveLikeNum;

    /**
      * 创建人
      */
    private Long createBy;

    /**
      * 创建时间
      */
@JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date createTime;

}
