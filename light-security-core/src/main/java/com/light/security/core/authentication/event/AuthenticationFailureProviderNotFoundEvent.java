package com.light.security.core.authentication.event;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;

/**
 * @ClassName AuthenticationFailureProviderNotFoundEvent
 * @Description 指示由于没有注册的<code> AuthenticationProvider </ code>可以处理请求而导致身份验证失败的应用程序事件。
 * Application event which indicates authentication failure due to there being no
 * registered <code>AuthenticationProvider</code> that can process the request.
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class AuthenticationFailureProviderNotFoundEvent extends AbstractAuthenticationFailureEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param authentication the object on which the event initially occurred (never {@code null})
     * @param exception
     */
    public AuthenticationFailureProviderNotFoundEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}
