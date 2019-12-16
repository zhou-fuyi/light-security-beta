package com.light.security.core.authentication.handler;

import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName SimpleAppAuthenticationFailureHandler
 * @Description APP模式下(前后端分离)认证错误处理, 返回JSON
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class SimpleAppAuthenticationFailureHandler extends AbstractAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (logger.isDebugEnabled()){
            logger.debug("执行认证失败处理器");
        }
        ServletUtils.writeAuthenticationException(request, response, exception);
    }
}
