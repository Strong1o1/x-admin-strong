package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("activity")
public class Activity extends Model<Activity> {
    /**
      * ID主键
      */
@TableId(value = "act_id", type = IdType.AUTO)
    private Long actId;

    /**
      * 名称
      */
    private String actName;

    /**
      * 描述
      */
    private String description;

    /**
      * 截止报名时间
      */
@JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date stopTime;

    /**
      * 活动开始时间
      */
@JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date startTime;

    /**
      * 地点
      */
    private String place;

    /**
      * 实践分
      */
    private Integer grade;

    /**
     * 名额
     */
    private Integer needNum;

}
