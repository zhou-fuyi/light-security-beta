package com.light.security.core.access.handler;

import com.light.security.core.exception.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @InterfaceName AccessDeniedHandler
 * @Description 仿照SpringSecurity
 * Used by {@link com.light.security.core.filter.ExceptionTranslationFilter} to handle an <code>AccessDeniedException</code>.
 *
 * 给异常转换过滤器提供访问控制异常处理器
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public interface AccessDeniedHandler {

    /**
     * Handles an access denied failure.
     * 处理访问控制异常
     * @param request
     * @param response
     * @param accessDeniedException
     * @throws IOException
     * @throws ServletException
     */
    void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException;

}
