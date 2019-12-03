package com.light.security.core.authentication.event;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;

/**
 * @ClassName AuthenticationFailureBadCredentialsEvent
 * @Description 指示由于提供无效凭据而导致身份验证失败的应用程序事件。
 * Application event which indicates authentication failure due to invalid credentials
 * being presented.
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class AuthenticationFailureBadCredentialsEvent extends AbstractAuthenticationFailureEvent{
    /**
     * Create a new ApplicationEvent.
     *
     * @param authentication the object on which the event initially occurred (never {@code null})
     * @param exception
     */
    public AuthenticationFailureBadCredentialsEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}
