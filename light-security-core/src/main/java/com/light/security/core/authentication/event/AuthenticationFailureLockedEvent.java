package com.light.security.core.authentication.event;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;

/**
 * @ClassName AuthenticationFailureLockedEvent
 * @Description 指示由于用户帐户已被锁定而导致身份验证失败的应用程序事件。
 * Application event which indicates authentication failure due to the user's account
 * having been locked.
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class AuthenticationFailureLockedEvent extends AbstractAuthenticationFailureEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param authentication the object on which the event initially occurred (never {@code null})
     * @param exception
     */
    public AuthenticationFailureLockedEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}
