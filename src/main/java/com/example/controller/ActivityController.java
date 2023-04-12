package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
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
import com.example.entity.Activity;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.ActivityService;
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
@RequestMapping("/api/activity")
public class ActivityController {
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
    public Result<?> save(@RequestBody Activity activity) {
        return Result.success(activityService.save(activity));
    }

    @PutMapping
    public Result<?> update(@RequestBody Activity activity) {
        return Result.success(activityService.updateById(activity));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        activityService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(activityService.getById(id));
    }

    @GetMapping
    public Result<?> findAll() {
        return Result.success(activityService.list());
    }

    /**
     * 查询活动数量
     * TODO
     * @return
     */
    @GetMapping("/loadactnum")
    public Result<Integer> loadRoleNum() {
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        return Result.success(activityService.count(wrapper));
    }

    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Activity> query = Wrappers.<Activity>lambdaQuery().orderByAsc(Activity::getActId);
        if (StrUtil.isNotBlank(name)) {
            query.like(Activity::getActName, name);
        }
        return Result.success(activityService.page(new Page<>(pageNum, pageSize), query));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Map<String, Object>> list = CollUtil.newArrayList();

        List<Activity> all = activityService.list();
        for (Activity obj : all) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ID", obj.getActId());
            row.put("名称", obj.getActName());
            row.put("描述", obj.getDescription());
            row.put("截止报名时间", obj.getStopTime());
            row.put("活动开始时间", obj.getStartTime());
            row.put("地点", obj.getPlace());
            row.put("实践分", obj.getGrade());

            list.add(row);
        }

        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("活动信息", "UTF-8");
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
        List<Activity> saveList = new ArrayList<>();
        for (List<Object> row : lists) {
            Activity obj = new Activity();
            obj.setActId(Long.valueOf((String) row.get(1)));
            obj.setActName((String) row.get(2));
            obj.setDescription((String) row.get(3));
            obj.setStopTime(DateUtil.parseDateTime((String) row.get(4)));
            obj.setStartTime(DateUtil.parseDateTime((String) row.get(5)));
            obj.setPlace((String) row.get(6));
            obj.setGrade(Integer.valueOf((String) row.get(7)));

            saveList.add(obj);
        }
        activityService.saveBatch(saveList);
        return Result.success();
    }

}
