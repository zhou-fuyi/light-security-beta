package com.light.security.core.authentication.event;

import com.light.security.core.authentication.token.Authentication;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassName AbstractAuthenticationEvent
 * @Description 身份认证事件通用实现
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public abstract class AbstractAuthenticationEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param authentication the object on which the event initially occurred (never {@code null})
     */
    public AbstractAuthenticationEvent(Authentication authentication) {
        super(authentication);
    }

    public Authentication getAuthentication(){
        return (Authentication) super.getSource();
    }
}
