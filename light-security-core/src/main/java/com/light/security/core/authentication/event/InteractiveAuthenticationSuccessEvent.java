package com.light.security.core.authentication.event;

import com.light.security.core.authentication.token.Authentication;
import org.springframework.util.Assert;

/**
 * @ClassName InteractiveAuthenticationSuccessEvent
 * @Description 交互式身份验证成功事件
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class InteractiveAuthenticationSuccessEvent extends AbstractAuthenticationEvent {

    private final Class<?> generatedBy;

    /**
     * Create a new ApplicationEvent.
     *
     * @param authentication the object on which the event initially occurred (never {@code null})
     */
    public InteractiveAuthenticationSuccessEvent(Authentication authentication, Class<?> generatedBy) {
        super(authentication);
        Assert.notNull(generatedBy, "构造器不接受空值参数 --> generatedBy is null");
        this.generatedBy = generatedBy;
    }

    /**
     * 生成此事件的<code> Class </ code>的Getter。 这对于生成其他日志记录信息很有用。
     * @return
     */
    public Class<?> getGeneratedBy() {
        return generatedBy;
    }
}
