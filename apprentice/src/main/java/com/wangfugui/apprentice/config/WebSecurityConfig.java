package com.wangfugui.apprentice.config;

import com.wangfugui.apprentice.common.util.JwtTokenUtils;
import com.wangfugui.apprentice.config.filter.JWTAuthenticationFilter;
import com.wangfugui.apprentice.config.filter.JWTAuthorizationFilter;
import com.wangfugui.apprentice.config.springsecurity.CustomUserDetailsService;
import com.wangfugui.apprentice.config.springsecurity.JWTAccessDeniedHandler;
import com.wangfugui.apprentice.config.springsecurity.JWTAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDatailService;

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // post请求要关闭csrf验证,不然访问报错；实际开发中开启，需要前端配合传递其他参数
        http.csrf().disable()
                .authorizeRequests()
                //swagger
                .antMatchers("/swagger-ui.html").anonymous()
                .antMatchers("/swagger-resources/**").anonymous()
                .antMatchers("/webjars/**").anonymous()
                .antMatchers("/*/api-docs").anonymous()
                //设置哪些路径不需要认证,这里也能放行静态资源
                .antMatchers("/webSocket/**").anonymous()
                .antMatchers("/static/**").anonymous()
                .antMatchers("/css/**", "/js/**").anonymous()
                .antMatchers("/favicon.ico").anonymous()
                //放开注册,登录用户接口
                .antMatchers("/user/register").anonymous()
                .antMatchers("/login").anonymous()
                .antMatchers("/logoutSystem").anonymous()
                .antMatchers("/chatroom").anonymous()
                .antMatchers("/onlineusers").anonymous()
                .antMatchers("/currentuser").anonymous()
                .anyRequest().authenticated() // 所有请求都需要验证
                .and()     //这里采用链式编程
                .logout()
                //注销成功后，调转的页面
                .logoutSuccessUrl("/login")
                // 配置自己的注销URL，默认为 /logout
                .logoutUrl("/logoutSystem")
                // 是否销毁session，默认ture
                .invalidateHttpSession(true)
                //  删除指定的cookies
                .deleteCookies(JwtTokenUtils.TOKEN_HEADER)
                .and()
                //添加用户账号的认证
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                //添加用户权限的认证
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                //我们可以准确地控制什么时机创建session，有以下选项进行控制：
                //always – 如果session不存在总是需要创建；
                //ifRequired – 仅当需要时，创建session(默认配置)；
                //never – 框架从不创建session，但如果已经存在，会使用该session ；
                //stateless – Spring Security不会创建session，或使用session；
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                //添加没有携带token或者token无效操作
                .authenticationEntryPoint(new JWTAuthenticationEntryPoint())
                //添加无权限时的处理
                .accessDeniedHandler(new JWTAccessDeniedHandler());
    }

    /**
     * 指定加密方式
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt加密密码
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                // 从数据库读取的用户进行身份认证
                .userDetailsService(userDatailService)
                .passwordEncoder(passwordEncoder());
    }
//    @Override
//    protected void configure(HttpSecurity httpSecurity) throws Exception{
//        //调试阶段
//        httpSecurity.csrf().disable().authorizeRequests();
//        httpSecurity.authorizeRequests().anyRequest()
//                .permitAll().and().logout().permitAll();
//    }


}