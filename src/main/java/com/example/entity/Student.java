package com.example.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;


@Data
@TableName("student")
public class Student extends Model<Student> {
    /**
      * ID主键
      */
@TableId(value = "stu_id", type = IdType.AUTO)
    private Long stuId;

    /**
      * 姓名
      */
    private String stuName;

    /**
      * 学号
      */
    private String stuNumber;

    /**
      * 手机号
      */
    private String stuPhone;

    /**
      * 实践分
      */
    private Integer stuScore;

}