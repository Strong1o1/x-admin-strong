package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.Student;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.User;
import com.example.mapper.StudentMapper;
import com.example.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StudentService extends ServiceImpl<StudentMapper, Student> {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private UserMapper userMapper;

    public void insertStu() {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Student::getStuNumber, Student::getStuName, Student::getStuPhone);
        List<Student> students = studentMapper.selectList(queryWrapper);
        for (Student student : students) {
            String username = student.getStuNumber();
            String password = "Tjdr@" + username.substring(username.length() - 4);
            String NickName = student.getStuName();
            String phone = student.getStuPhone();
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setNickName(NickName);
            user.setPhone(phone);
            user.setAvatar("可达鸭头像.jpg");
            userMapper.insert(user);
        }
    }

    public void joinAct(String stuNum,Integer grade) {
        if (!"admin".equals(stuNum)) {
            LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Student::getStuNumber,stuNum);
            Student student = studentMapper.selectOne(queryWrapper);
            student.setStuScore(student.getStuScore() + grade);
            studentMapper.update(student, queryWrapper);
        }
    }

}
