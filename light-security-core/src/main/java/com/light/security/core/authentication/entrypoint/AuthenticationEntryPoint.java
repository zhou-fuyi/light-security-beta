package com.light.security.core.authentication.entrypoint;


import com.light.security.core.exception.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @InterfaceName AuthenticationEntryPoint
 * @Description 仿照 SpringSecurity
 * Used by {@link com.light.security.core.filter.ExceptionTranslationFilter} to commence an authentication scheme.
 *
 * 该接口是为了处理ExceptionTranslationFilter之后出现的AuthenticationException
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public interface AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme.
     * <p>
     * <code>ExceptionTranslationFilter</code> will populate the <code>HttpSession</code>
     * attribute named
     * <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
     * with the requested target URL before calling this method.
     * <p>
     * Implementations should modify the headers on the <code>ServletResponse</code> as
     * necessary to commence the authentication process.
     *
     *
     * 翻译:
     * 开始身份验证方案。
     * <p>
     * <code> ExceptionTranslationFilter </ code>将填充<code> HttpSession </ code>
     * 属性命名
     * <code> AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY </ code>
     * 在调用此方法之前，使用请求的目标URL。
     * <p>
     * 实现应根据需要修改<code> ServletResponse </ code>上的标头，以开始身份验证过程。
     *
     * 该接口是为了处理ExceptionTranslationFilter之后出现的AuthenticationException
     *
     * @param request that resulted in an <code>AuthenticationException</code>
     * @param response so that the user agent can begin authentication
     * @param authException that caused the invocation
     *
     */
    void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException;
}
