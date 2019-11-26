package com.light.security.core.access.handler;

import com.light.security.core.exception.AccessDeniedException;
import com.light.security.core.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName AccessDeniedHandlerImpl
 * @Description {@link AccessDeniedHandler}的默认实现
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    protected static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // TODO: 2019-11-26 目前进行简单处理, 暂时不考虑前后端未分离的场景
        //关于前后端为分离的场景, 可以参考SpringSecurity中的AccessDeniedHandlerImpl#handle实现
        ServletUtils.writeAccessDeniedException(request, response, accessDeniedException);
    }
}
