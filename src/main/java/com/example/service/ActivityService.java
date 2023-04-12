package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.Activity;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.ActivityMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ActivityService extends ServiceImpl<ActivityMapper, Activity> {

    @Resource
    private ActivityMapper activityMapper;

    public void joinAct(Integer actId, Integer needNum) {
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getActId,actId);
        Activity activity = activityMapper.selectOne(queryWrapper);
        activity.setNeedNum(activity.getNeedNum() - 1);
        activityMapper.update(activity,queryWrapper);
    }
}
