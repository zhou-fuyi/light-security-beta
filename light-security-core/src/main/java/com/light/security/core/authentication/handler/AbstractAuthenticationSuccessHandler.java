package com.light.security.core.authentication.handler;

import com.light.security.core.authentication.token.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName AbstractAuthenticationSuccessHandler
 * @Description 认证成功处理器通用实现
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public abstract class AbstractAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractAuthenticationSuccessHandler(){}

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (logger.isWarnEnabled()){
            logger.warn("请定义自己的认证成功处理器, 默认成功处理器开发中...");
        }
    }
}
