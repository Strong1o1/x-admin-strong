package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Comment;
import com.example.entity.Communicate;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.CommentService;
import com.example.service.CommunicateService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("/api/communicate")
public class CommunicateController {
    @Resource
    private CommunicateService communicateService;
    @Resource
    private CommentService commentService;
    @Resource
    private HttpServletRequest request;

    public User getUser() {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomException("-1", "请登录");
        }
        return user;
    }

    /**
     * 评论动态
     * TODO
     * @param comment
     * @return
     */
    @PostMapping
    public Result<?> addComment(@RequestBody Comment comment) {
        comment.setCreateBy(getUser().getId());
        comment.setCreateTime(new Date());
        commentService.save(comment);
        return Result.success();
    }

    /**
     * 发布动态
     * TODO
     * @param communicate
     * @return
     */
    @PostMapping("/show")
    public Result<?> show(@RequestBody Communicate communicate) {
//        communicate.setCommentId(System.currentTimeMillis());
        communicate.setCreateBy(getUser().getId());
        communicate.setCreateTime(new Date());
        return Result.success(communicateService.save(communicate));
    }

    @PutMapping
    public Result<?> update(@RequestBody Communicate communicate) {
        return Result.success(communicateService.updateById(communicate));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        communicateService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(communicateService.getById(id));
    }

    /**
     * 点赞
     * @param communicateId
     * @return
     */
    @GetMapping("/givelike/{communicateId}")
    public Result<?> giveLike(@PathVariable String communicateId) {
        long coId = Long.parseLong(communicateId);
        communicateService.giveLike(coId);
        return Result.success();
    }

    /**
     * 取消点赞
     * @param communicateId
     * @return
     */
    @GetMapping("/takelike/{communicateId}")
    public Result<?> takeLike(@PathVariable String communicateId) {
        long coId = Long.parseLong(communicateId);
        communicateService.takeLike(coId);
        return Result.success();
    }

    @GetMapping
    public Result<?> findAll() {
        return Result.success(communicateService.list());
    }

    @GetMapping("/page")
    public Result<?> findPage(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        return communicateService.findPage(pageNum, pageSize);
    }

    /**
     * 查询对应动态的评论
     * TODO
     * @param pageNum
     * @param pageSize
     * @param communicateId
     * @return
     */
    @GetMapping("/commentList")
    public Result<?> commentList(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam Long communicateId) {

        return commentService.commentList(pageNum, pageSize,communicateId);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Map<String, Object>> list = CollUtil.newArrayList();

        List<Communicate> all = communicateService.list();
        for (Communicate obj : all) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("动态ID", obj.getCommunicateId());
            row.put("内容", obj.getContent());
            row.put("图片", obj.getPicture());
            row.put("点赞数", obj.getGiveLikeNum());
            row.put("创建人", obj.getCreateBy());
            row.put("创建时间", obj.getCreateTime());

            list.add(row);
        }

        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("校友圈信息", "UTF-8");
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
        List<Communicate> saveList = new ArrayList<>();
        for (List<Object> row : lists) {
            Communicate obj = new Communicate();
            obj.setCommunicateId(Long.valueOf((String) row.get(1)));
            obj.setContent((String) row.get(2));
            obj.setPicture((String) row.get(3));
            obj.setGiveLikeNum(Integer.valueOf((String) row.get(5)));
            obj.setCreateBy(Long.valueOf((String) row.get(6)));
            obj.setCreateTime(DateUtil.parseDateTime((String) row.get(7)));

            saveList.add(obj);
        }
        communicateService.saveBatch(saveList);
        return Result.success();
    }

}
