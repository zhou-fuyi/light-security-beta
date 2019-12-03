package com.light.security.core.authentication.event;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;

/**
 * @ClassName AuthenticationFailureServiceExceptionEvent
 * @Description 指示由于<code> AuthenticationManager </ code>内部存在问题而导致身份验证失败的应用程序事件。
 * Application event which indicates authentication failure due to there being a problem
 * internal to the <code>AuthenticationManager</code>.
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class AuthenticationFailureServiceExceptionEvent extends AbstractAuthenticationFailureEvent{
    /**
     * Create a new ApplicationEvent.
     *
     * @param authentication the object on which the event initially occurred (never {@code null})
     * @param exception
     */
    public AuthenticationFailureServiceExceptionEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}
