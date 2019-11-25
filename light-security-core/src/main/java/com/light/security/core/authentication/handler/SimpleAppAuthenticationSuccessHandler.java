package com.light.security.core.authentication.handler;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName SimpleAppAuthenticationSuccessHandler
 * @Description APP模式下(前后端分离)认证成功处理, 返回JSON
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class SimpleAppAuthenticationSuccessHandler extends AbstractAuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (logger.isDebugEnabled()){
            logger.debug("执行认证成功处理器");
        }
        ServletUtils.writeAuthenticationSuccess(request, response, authentication);
    }
}
