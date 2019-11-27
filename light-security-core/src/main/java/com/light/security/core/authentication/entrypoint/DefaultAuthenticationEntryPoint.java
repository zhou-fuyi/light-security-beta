package com.light.security.core.authentication.entrypoint;

import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName DefaultAuthenticationEntryPoint
 * @Description <code> AuthenticationEntryPoint <code/> 的默认实现
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public class DefaultAuthenticationEntryPoint extends AbstractAuthenticationEntryPoint {

    /**
     * 这里的处理与AuthenticationFailureHandler中的默认处理相同, 但是该接口的存在意义不同, 该接口是为了处理ExceptionTranslationFilter之后出现的AuthenticationException
     * 这样的解决方案符合前后端分离场景
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ServletUtils.writeAuthenticationException(request, response, authException);
    }
}
