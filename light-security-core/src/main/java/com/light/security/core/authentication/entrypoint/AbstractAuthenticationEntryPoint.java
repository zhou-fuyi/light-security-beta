package com.light.security.core.authentication.entrypoint;

import com.light.security.core.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName AbstractAuthenticationEntryPoint
 * @Description <code> AuthenticationEntryPoint </code>的通用实现, 暂时看上去好像没啥用, 先留着吧, 万一呢
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public abstract class AbstractAuthenticationEntryPoint implements AuthenticationEntryPoint {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.warn("请实现自己的开始认证方案");
    }
}
