package com.light.security.core.authentication.event;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;
import org.springframework.util.Assert;

/**
 * @ClassName AbstractAuthenticationFailureEvent
 * @Description Abstract application event which indicates authentication failure for some reason.
 * 由于某种原因指示认证失败的抽象应用程序事件。
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public abstract class AbstractAuthenticationFailureEvent extends AbstractAuthenticationEvent {

    private final AuthenticationException exception;

    /**
     * Create a new ApplicationEvent.
     *
     * @param authentication the object on which the event initially occurred (never {@code null})
     */
    public AbstractAuthenticationFailureEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication);
        Assert.notNull(exception, "构造器不接受空值参数 --> exception is null");
        this.exception = exception;
    }

    public AuthenticationException getException() {
        return exception;
    }
}
