package com.wangfugui.apprentice.controller;

import com.wangfugui.apprentice.common.util.ResponseUtils;
import com.wangfugui.apprentice.dao.domain.User;
import com.wangfugui.apprentice.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/23
 * @since JDK 1.8.0
 */
@RestController
@Api(tags = "用户管理")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("查询所有用户")
    @GetMapping("/listUser")
    public ResponseUtils listUser() {
        return userService.listUser();
    }

    @ApiOperation("根据名称查询用户")
    @GetMapping("/getUser")
    public User getUser(@RequestParam String username){
        return userService.getUserInfo(username);
    }

    @PreAuthorize("hasAnyRole('test')") // 只能test角色才能访问该方法
    @GetMapping("/test")
    public String test(){
        return "test角色访问";
    }

    @PreAuthorize("hasAnyRole('admin')") // 只能admin角色才能访问该方法
    @GetMapping("/admin")
    public String admin(){
        return "admin角色访问";
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public ResponseUtils register(@RequestBody User userInfo){
        return userService.insertUser(userInfo);
    }

    @PutMapping("/updatePwd")
    @ApiOperation("更新密码")
    public ResponseUtils updatePwd(@RequestBody Map<String, String> map){
        return userService.updatePwd(map.get("oldPwd"), map.get("newPwd"));
    }
}
