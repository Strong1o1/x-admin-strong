package com.example.service;

import com.example.entity.Book;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.BookMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BookService extends ServiceImpl<BookMapper, Book> {

    @Resource
    private BookMapper bookMapper;

}
