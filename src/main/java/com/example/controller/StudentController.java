package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Student;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.ActivityService;
import com.example.service.StudentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Resource
    private StudentService studentService;
    @Resource
    private ActivityService activityService;
    @Resource
    private HttpServletRequest request;

    public User getUser() {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomException("-1", "请登录");
        }
        return user;
    }

    @PostMapping
    public Result<?> save(@RequestBody Student student) {
        return Result.success(studentService.save(student));
    }

    @PutMapping
    public Result<?> update(@RequestBody Student student) {
        return Result.success(studentService.updateById(student));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        studentService.removeById(id);
        return Result.success();
    }

    /**
     * 排行
     * TODO
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/rank")
    public Result<?> findRank(@RequestParam(required = false, defaultValue = "") String name,
                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Student> query = Wrappers.<Student>lambdaQuery().orderByDesc(Student::getStuScore);
        if (StrUtil.isNotBlank(name)) {
            query.like(Student::getStuName, name);
        }

        return Result.success(studentService.page(new Page<>(pageNum, pageSize), query));
    }

    /**
     * 报名活动
     * TODO
     * @param username
     * @return
     */
    @PostMapping("/joinAct")
    public Result<User> joinAct(String username,Integer grade,Integer actId,Integer needNum) {
        if (needNum > 0){
            studentService.joinAct(username,grade);
            activityService.joinAct(actId,needNum);
            return Result.success();
        }
        return Result.error("250","没有名额！！！");
    }

    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(studentService.getById(id));
    }

    @GetMapping
    public Result<?> findAll() {
        return Result.success(studentService.list());
    }

    /**
     * 查询学生数量
     * TODO
     * @return
     */
    @GetMapping("/loadstunum")
    public Result<Integer> loadStudentNum() {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        return Result.success(studentService.count(wrapper));
    }

    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Student> query = Wrappers.<Student>lambdaQuery().orderByAsc(Student::getStuId);
        if (StrUtil.isNotBlank(name)) {
            query.like(Student::getStuName, name);
        }
        return Result.success(studentService.page(new Page<>(pageNum, pageSize), query));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Map<String, Object>> list = CollUtil.newArrayList();

        List<Student> all = studentService.list();
        for (Student obj : all) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ID", obj.getStuId());
            row.put("姓名", obj.getStuName());
            row.put("学号", obj.getStuNumber());
            row.put("手机号", obj.getStuPhone());
            row.put("实践分", obj.getStuScore());

            list.add(row);
        }

        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("学生信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);
    }

    @GetMapping("/upload/{fileId}")
    public Result<?> upload(@PathVariable String fileId) {
        String basePath = System.getProperty("user.dir") + "/src/main/resources/static/file/";
        List<String> fileNames = FileUtil.listFileNames(basePath);
        String file = fileNames.stream().filter(name -> name.contains(fileId)).findAny().orElse("");
        List<List<Object>> lists = ExcelUtil.getReader(basePath + file).read(1);
        List<Student> saveList = new ArrayList<>();
        for (List<Object> row : lists) {
            Student obj = new Student();
            obj.setStuId(Long.valueOf((String) row.get(1)));
            obj.setStuName((String) row.get(2));
            obj.setStuNumber((String) row.get(3));
            obj.setStuPhone((String) row.get(4));
            obj.setStuScore(Integer.valueOf((String) row.get(5)));

            saveList.add(obj);
        }
        studentService.saveBatch(saveList);
        return Result.success();
    }

}
