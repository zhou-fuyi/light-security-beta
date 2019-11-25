package com.light.security.core.authentication.handler;

import com.light.security.core.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName AbstractAuthenticationFailureHandler
 * @Description 认证失败处理器通用实现
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public abstract class AbstractAuthenticationFailureHandler implements AuthenticationFailureHandler{

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractAuthenticationFailureHandler(){}


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (logger.isWarnEnabled()){
            logger.warn("请定义自己的认证失败处理器, 默认错误处理器开发中...");
        }
    }
}
