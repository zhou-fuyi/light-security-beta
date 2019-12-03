package com.light.security.core.authentication.event;

import com.light.security.core.authentication.token.Authentication;

/**
 * @ClassName AuthenticationSuccessEvent
 * @Description 表示成功认证的应用程序事件。
 * Application event which indicates successful authentication.
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class AuthenticationSuccessEvent extends AbstractAuthenticationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param authentication the object on which the event initially occurred (never {@code null})
     */
    public AuthenticationSuccessEvent(Authentication authentication) {
        super(authentication);
    }
}
