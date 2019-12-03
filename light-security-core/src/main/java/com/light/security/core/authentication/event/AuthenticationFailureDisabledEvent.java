package com.light.security.core.authentication.event;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;

/**
 * @ClassName AuthenticationFailureDisabledEvent
 * @Description 指示由于禁用用户帐户而导致身份验证失败的应用程序事件。
 * Application event which indicates authentication failure due to the user's account
 * being disabled.
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class AuthenticationFailureDisabledEvent extends AbstractAuthenticationFailureEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param authentication the object on which the event initially occurred (never {@code null})
     * @param exception
     */
    public AuthenticationFailureDisabledEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}
