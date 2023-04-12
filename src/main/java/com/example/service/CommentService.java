package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.Result;
import com.example.entity.Comment;
import com.example.entity.User;
import com.example.entity.vo.CommentVo;
import com.example.entity.vo.PageVo;
import com.example.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService extends ServiceImpl<CommentMapper, Comment> {

    @Resource
    private UserService userService;

    public Result<?> commentList(Integer pageNum, Integer pageSize, Long communicateId) {
        //1.查询对应动态的根评论

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //1.1对commentId进行判断
        queryWrapper.eq(Comment::getCommunicateId,communicateId);
        //1.2根评论 rootId为-1
        queryWrapper.eq(Comment::getRootId, -1);
        //2.分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        page(page,queryWrapper);

        List<Comment> commentList = page.getRecords();
        ArrayList<CommentVo> CommentVoList = new ArrayList<CommentVo>();
        ArrayList<CommentVo> childrenCommentVoList = new ArrayList<CommentVo>();
        for (Comment comment : commentList) {
            CommentVo CommentVo = new CommentVo();
            CommentVo.setCommentId(comment.getCommentId());
            User one = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId,comment.getCreateBy()));
            CommentVo.setAvatar(one.getAvatar());
            CommentVo.setRootId(comment.getRootId());
            CommentVo.setContent(comment.getContent());
            if (comment.getToCommentUserId() != -1){
                CommentVo.setToCommentUserName(userService.getById(comment.getToCommentUserId()).getNickName());
            }
            CommentVo.setToCommentId(comment.getToCommentId());
            CommentVo.setUserId(comment.getCreateBy());
            CommentVo.setUsername(userService.getById(comment.getCreateBy()).getNickName());
            CommentVo.setCreateTime(comment.getCreateTime());
            //根据根评论id查询子评论
            List<Comment> childrenCommentList = list(new LambdaQueryWrapper<Comment>().eq(Comment::getRootId,comment.getCommentId()));
            if (childrenCommentList.size() != 0) {
                for (Comment childrenComment : childrenCommentList) {
                    CommentVo childrenCommentVo = new CommentVo();
                    childrenCommentVo.setCommentId(childrenComment.getCommentId());
                    User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId, childrenComment.getCreateBy()));
                    childrenCommentVo.setAvatar(user.getAvatar());
                    childrenCommentVo.setRootId(childrenComment.getRootId());
                    childrenCommentVo.setContent(childrenComment.getContent());
                    if (childrenComment.getToCommentUserId() != -1) {
                        childrenCommentVo.setToCommentUserName(userService.getById(childrenComment.getToCommentUserId()).getNickName());
                    }
                    childrenCommentVo.setToCommentId(childrenComment.getToCommentId());
                    childrenCommentVo.setUserId(childrenComment.getCreateBy());
                    childrenCommentVo.setUsername(userService.getById(childrenComment.getCreateBy()).getNickName());
                    childrenCommentVo.setCreateTime(childrenComment.getCreateTime());
                    childrenCommentVoList.add(childrenCommentVo);
                }
                CommentVo.setChildren(childrenCommentVoList);
                childrenCommentVoList = new ArrayList<CommentVo>();
            }
            CommentVoList.add(CommentVo);
        }
//        Page<CommentVo> pageVo = new Page<>(pageNum, pageSize);
//        pageVo.setRecords(CommentVoList);
//        pageVo.setTotal(page.getTotal());

        return Result.success(new PageVo(CommentVoList,page.getTotal()));
    }

}
