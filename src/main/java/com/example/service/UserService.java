package com.example.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.entity.vo.MapVo;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private PermissionService permissionService;

    public User login(User user) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword());
        User one = getOne(queryWrapper);
        if (one == null) {
            throw new CustomException("-1", "账号或密码错误");
        }
        one.setPermission(getPermissions(one.getId()));
        return one;
    }

    public User register(User user) {
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername())));
        if (one != null) {
            throw new CustomException("-1", "用户已注册");
        }
        if (user.getPassword() == null) {
            user.setPassword("123456");
        }
        user.setRole(CollUtil.newArrayList(2L));  // 默认普通用户角色
        save(user);
        return getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername())));
    }

    /**
     * 设置权限
     * @param userId
     * @return
     */
    public List<Permission> getPermissions(Long userId) {
        User user = getById(userId);
        List<Permission> permissions = new ArrayList<>();
        List<Long> role = user.getRole();
        if (role != null) {
            for (Object roleId : role) {
                Role realRole = roleService.getById((int) roleId);
                if (CollUtil.isNotEmpty(realRole.getPermission())) {
                    for (Object permissionId : realRole.getPermission()) {
                        Permission permission = permissionService.getById((int) permissionId);
                        if (permission != null && permissions.stream().noneMatch(p -> p.getPath().equals(permission.getPath()))) {
                            permissions.add(permission);
                        }
                    }
                }
            }
            user.setPermission(permissions);
        }
        return permissions;
    }

    public User getbyUsername(String username) {
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, username)));
        one.setPermission(getPermissions(one.getId()));
        return one;
    }

    public void deleteStu() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(User::getId,1);
        userMapper.delete(queryWrapper);
    }

    public String loadAvatar(Long userId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,userId);
        return userMapper.selectOne(queryWrapper).getAvatar();
    }

    public Set<MapVo> loadMap() {
        Set<MapVo> mapVos = userMapper.loadMap();
        HashSet<MapVo> mapVos1 = new HashSet<>();

//        Iterator<MapVo> iterator = mapVos.iterator();
//        while (iterator.hasNext()) {
//            MapVo mapVo = iterator.next();
//            if (mapVo.getName() != null) {
//                mapVos1.add(mapVo);
//            }
//        }

        mapVos1.add(new MapVo("北京",100));
        mapVos1.add(new MapVo("天津",0));
        mapVos1.add(new MapVo("上海",0));
        mapVos1.add(new MapVo("重庆",0));
        mapVos1.add(new MapVo("河北",0));
        mapVos1.add(new MapVo("河南",0));
        mapVos1.add(new MapVo("云南",0));
        mapVos1.add(new MapVo("辽宁",0));
        mapVos1.add(new MapVo("黑龙江",0));
        mapVos1.add(new MapVo("湖南",0));
        mapVos1.add(new MapVo("安徽",0));
        mapVos1.add(new MapVo("山东",0));
        mapVos1.add(new MapVo("新疆",0));
        mapVos1.add(new MapVo("江苏",0));
        mapVos1.add(new MapVo("浙江",0));
        mapVos1.add(new MapVo("江西",0));
        mapVos1.add(new MapVo("湖北",0));
        mapVos1.add(new MapVo("广西",0));
        mapVos1.add(new MapVo("甘肃",0));
        mapVos1.add(new MapVo("山西",0));
        mapVos1.add(new MapVo("内蒙古",0));
        mapVos1.add(new MapVo("陕西",0));
        mapVos1.add(new MapVo("吉林",0));
        mapVos1.add(new MapVo("福建",0));
        mapVos1.add(new MapVo("贵州",0));
        mapVos1.add(new MapVo("广东",0));
        mapVos1.add(new MapVo("青海",0));
        mapVos1.add(new MapVo("西藏",0));
        mapVos1.add(new MapVo("四川",0));
        mapVos1.add(new MapVo("宁夏",0));
        mapVos1.add(new MapVo("海南",0));
        mapVos1.add(new MapVo("台湾",0));
        mapVos1.add(new MapVo("香港",0));
        mapVos1.add(new MapVo("澳门",0));

        return mapVos1;
    }
}
