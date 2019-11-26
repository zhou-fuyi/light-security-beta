package com.light.security.core.authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName WebAuthenticationDetailsSource
 * @Description web认证时细节存储源
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public class WebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {
    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new WebAuthenticationDetails(context);
    }
}
