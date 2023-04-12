package com.example.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;

@Data
@TableName("book")
public class Book extends Model<Book> {
    /**
      * 序号主键 
      */
@TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 名称 
      */
    private String name;

    /**
      * 价格 
      */
    private BigDecimal price;

}