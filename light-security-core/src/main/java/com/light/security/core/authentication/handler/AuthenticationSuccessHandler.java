package com.light.security.core.authentication.handler;

import com.light.security.core.authentication.token.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @InterfaceName AuthenticationSuccessHandler
 * @Description 主体认证成功处理器接口
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public interface AuthenticationSuccessHandler {

    /**
     * 认证成功处理方法
     * @param request 认证请求
     * @param response 认证响应
     * @param authentication 认证成功后主体数据对象
     * @throws IOException
     * @throws ServletException
     */
    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException;

}
