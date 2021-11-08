package com.wangfugui.apprentice.config.springsecurity;

import com.alibaba.fastjson.JSONObject;
import com.wangfugui.apprentice.common.constant.CodeEnums;
import com.wangfugui.apprentice.common.util.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 没有携带token或者token无效
 * @author MrFugui 2021年10月30日10:55:21
 */
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException exception) throws IOException, ServletException {

        String result = "";
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        if (exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result = JSONObject.toJSONString(ResponseUtils.msg(CodeEnums.PASSWORD_ERROR.getMsg()));
        } else if (exception instanceof InsufficientAuthenticationException
                || exception instanceof NonceExpiredException) {
            result = JSONObject.toJSONString(ResponseUtils.msg(CodeEnums.AUTH_ERROR.getMsg()));
        } else if (exception instanceof UsernameNotFoundException) {
            result = JSONObject.toJSONString(ResponseUtils.msg(CodeEnums.NO_USER.getMsg()));
        } else {
            result = "系统异常。";
        }
        response.getWriter().write(result);
    }
}
