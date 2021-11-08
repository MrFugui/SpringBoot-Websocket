package com.wangfugui.apprentice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wangfugui.apprentice.common.util.ResponseUtils;
import com.wangfugui.apprentice.dao.domain.User;
import com.wangfugui.apprentice.dao.mapper.UserMapper;
import com.wangfugui.apprentice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/23
 * @since JDK 1.8.0
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseUtils listUser() {
        List<User> users = userMapper.selectList(null);
        return ResponseUtils.success(users);
    }

    @Override
    public User getUserInfo(String username){
        QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("username",username);
        return userMapper.selectOne(objectQueryWrapper);
    }

    @Override
    public ResponseUtils insertUser(User userInfo){
        // 加密密码
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        return ResponseUtils.success(userMapper.insert(userInfo));
    }

    @Override
    public ResponseUtils updatePwd(String oldPwd, String newPwd) {
        // 获取当前登录用户信息(注意：没有密码的)
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();

        // 通过用户名获取到用户信息（获取密码）
        User userInfo = this.getUserInfo(username);

        // 判断输入的旧密码是正确
        if (passwordEncoder.matches(oldPwd, userInfo.getPassword())) {
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.lambda().eq(User::getUsername, username);
            //加密新密码
            String encode = passwordEncoder.encode(newPwd);
            userInfo.setPassword(encode);
            userMapper.update(userInfo, wrapper);
        }
        return ResponseUtils.success();
    }

    @Override
    public User getIdByUserName(String username) {

        return  userMapper.getIdByUserName(username);
    }


    @Override
    public String getpwdbyname(String name) {
        User s=userMapper.getpwdbyname(name);
        if(s!=null) {
            return s.getPassword();
        } else {
            return null;
        }
    }

    @Override
    public Long getUidbyname(String name) {
        User s=userMapper.getpwdbyname(name);
        if(s!=null) {
            return (long) s.getId();
        } else {
            return null;
        }
    }

    @Override
    public String getnamebyid(long id) {
        User s=userMapper.getnamebyid(id);
        if(s!=null) {
            return s.getUsername();
        } else {
            return null;
        }
    }

}
