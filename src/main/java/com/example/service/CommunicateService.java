package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.Result;
import com.example.entity.Communicate;
import com.example.entity.User;
import com.example.entity.vo.CommunicateVo;
import com.example.entity.vo.PageVo;
import com.example.mapper.CommunicateMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommunicateService extends ServiceImpl<CommunicateMapper, Communicate> {

    @Resource
    private CommunicateMapper communicateMapper;
    @Resource
    private UserService userService;

    public Result<?> findPage(Integer pageNum, Integer pageSize) {
        //1.查询对应用户的动态

        LambdaQueryWrapper<Communicate> queryWrapper = new LambdaQueryWrapper<>();
        //2.分页查询
        Page<Communicate> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        List<Communicate> communicateList = page.getRecords();
        ArrayList<CommunicateVo> communicateVoList = new ArrayList<CommunicateVo>();
        for (Communicate communicate : communicateList) {
            CommunicateVo communicateVo = new CommunicateVo();
            communicateVo.setCommunicateId(communicate.getCommunicateId());
            User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId, communicate.getCreateBy()));
            communicateVo.setAvatar(user.getAvatar());
            communicateVo.setGiveLikeNum(communicate.getGiveLikeNum());
            communicateVo.setNickName(user.getNickName());
            communicateVo.setContent(communicate.getContent());
            communicateVo.setPicture(communicate.getPicture());
            communicateVo.setCreateTime(communicate.getCreateTime());
            communicateVoList.add(communicateVo);
        }
        return Result.success(new PageVo(communicateVoList,page.getTotal()));
    }

    public void deleteStu() {
        LambdaQueryWrapper<Communicate> queryWrapper = new LambdaQueryWrapper<>();
        communicateMapper.delete(queryWrapper);
    }

    public void giveLike(Long communicateId) {
        LambdaQueryWrapper<Communicate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Communicate::getCommunicateId,communicateId);
        Communicate communicate = communicateMapper.selectOne(queryWrapper);
        communicate.setGiveLikeNum(communicate.getGiveLikeNum() + 1);
        communicateMapper.update(communicate,queryWrapper);
    }

    public void takeLike(long communicateId) {
        LambdaQueryWrapper<Communicate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Communicate::getCommunicateId,communicateId);
        Communicate communicate = communicateMapper.selectOne(queryWrapper);
        communicate.setGiveLikeNum(communicate.getGiveLikeNum() - 1);
        communicateMapper.update(communicate,queryWrapper);
    }


}
