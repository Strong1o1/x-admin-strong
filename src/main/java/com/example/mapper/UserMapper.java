package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.User;
import com.example.entity.vo.MapVo;

import java.util.Set;

public interface UserMapper extends BaseMapper<User> {

    Set<MapVo> loadMap();
}
