package com.wangfugui.apprentice.config.springsecurity;

import com.wangfugui.apprentice.dao.domain.User;
import com.wangfugui.apprentice.service.RoleService;
import com.wangfugui.apprentice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userInfoService;
    @Autowired
    private RoleService roleService;

    /**
     * 需新建配置类注册一个指定的加密方式Bean，或在下一步Security配置类中注册指定
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过用户名从数据库获取用户信息
        User userInfo = userInfoService.getUserInfo(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 得到用户角色
        Integer roleId = userInfo.getRoleId();
        // 角色集合
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (!ObjectUtils.isEmpty(roleId)) {
            String role = roleService.getUserRole(roleId);
            // 角色必须以`ROLE_`开头，数据库中没有，则在这里加
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        return new JwtUser(userInfo,authorities);
    }
}
