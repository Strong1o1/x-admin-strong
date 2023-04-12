package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.Result;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.entity.vo.MessageVo;
import com.example.entity.vo.PageVo;
import com.example.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private UserService userService;

//    public List<Message> findByForeign() {
//        LambdaQueryWrapper<Message> queryWrapper = Wrappers.<Message>lambdaQuery().eq(Message::getForeignId, 0).orderByDesc(Message::getId);
//        List<Message> list = list(queryWrapper);
//        for (Message Message : list) {
//            User one = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, Message.getUsername()));
//            Message.setAvatar("http://localhost:9999/files/" + one.getAvatar());
//            Long parentId = Message.getParentId();
//            list.stream().filter(c -> c.getId().equals(parentId)).findFirst().ifPresent(Message::setParentMessage);
//        }
//        return list;
//    }

    public Result<?> commentList(Integer pageNum, Integer pageSize) {
        //1.查询对应用户的根评论

        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        //1.1对userId进行判断
//        queryWrapper.eq(Message::getCreateBy,userId);
        //1.2根评论 rootId为-1
        queryWrapper.eq(Message::getRootId, -1);
        //2.分页查询
        Page<Message> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        List<Message> commentList = page.getRecords();
        ArrayList<MessageVo> messageVoList = new ArrayList<MessageVo>();
        ArrayList<MessageVo> childrenMessageVoList = new ArrayList<MessageVo>();
        for (Message message : commentList) {
            MessageVo messageVo = new MessageVo();
            messageVo.setId(message.getId());
            User one = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId,message.getCreateBy()));
            messageVo.setAvatar(one.getAvatar());
            messageVo.setRootId(message.getRootId());
            messageVo.setContent(message.getContent());
            if (message.getToCommentUserId() != -1){
                messageVo.setToCommentUserName(userService.getById(message.getToCommentUserId()).getNickName());
            }
            messageVo.setToCommentId(message.getToCommentId());
            messageVo.setUserId(message.getCreateBy());
            messageVo.setUsername(userService.getById(message.getCreateBy()).getNickName());
            messageVo.setCreateTime(message.getCreateTime());
            //根据根评论id查询子评论
            List<Message> childrenMessageList = list(new LambdaQueryWrapper<Message>().eq(Message::getRootId,message.getId()));
            if (childrenMessageList.size() != 0) {
                for (Message childrenMessage : childrenMessageList) {
                    MessageVo childrenMessageVo = new MessageVo();
                    childrenMessageVo.setId(childrenMessage.getId());
                    User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId, childrenMessage.getCreateBy()));
                    childrenMessageVo.setAvatar(user.getAvatar());
                    childrenMessageVo.setRootId(childrenMessage.getRootId());
                    childrenMessageVo.setContent(childrenMessage.getContent());
                    if (childrenMessage.getToCommentUserId() != -1) {
                        childrenMessageVo.setToCommentUserName(userService.getById(childrenMessage.getToCommentUserId()).getNickName());
                    }
                    childrenMessageVo.setToCommentId(childrenMessage.getToCommentId());
                    childrenMessageVo.setUserId(childrenMessage.getCreateBy());
                    childrenMessageVo.setUsername(userService.getById(childrenMessage.getCreateBy()).getNickName());
                    childrenMessageVo.setCreateTime(childrenMessage.getCreateTime());
                    childrenMessageVoList.add(childrenMessageVo);
                }
                messageVo.setChildren(childrenMessageVoList);
                System.out.println(messageVo.getChildren());
                childrenMessageVoList = new ArrayList<MessageVo>();
            }
            messageVoList.add(messageVo);
        }
//        Page<MessageVo> pageVo = new Page<>(pageNum, pageSize);
//        pageVo.setRecords(messageVoList);
//        pageVo.setTotal(page.getTotal());

        return Result.success(new PageVo(messageVoList,page.getTotal()));
    }

    public void deleteStu() {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        messageMapper.delete(queryWrapper);
    }
}
