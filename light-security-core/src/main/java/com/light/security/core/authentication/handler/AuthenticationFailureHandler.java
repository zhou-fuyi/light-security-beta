package com.light.security.core.authentication.handler;

import com.light.security.core.exception.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @InterfaceName AuthenticationFailureHandler
 * @Description 主体认证失败处理器接口
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public interface AuthenticationFailureHandler {

    /**
     * 认证失败处理方法
     * @param request 认证请求
     * @param response 认证响应
     * @param exception 导致失败的异常
     * @throws IOException
     * @throws ServletException
     */
    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException;

}
