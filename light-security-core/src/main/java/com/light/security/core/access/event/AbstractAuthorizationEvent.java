package com.light.security.core.access.event;

import org.springframework.context.ApplicationEvent;

/**
 * @ClassName AbstractAuthorizationEvent
 * @Description access包内<code>ApplicationEvent</code>的抽象实现
 * 作为所有权限拦截有关的事件的抽象超类
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public abstract class AbstractAuthorizationEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AbstractAuthorizationEvent(Object source) {
        super(source);
    }
}
