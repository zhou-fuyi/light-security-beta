package com.light.security.core.access.event;

import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.authentication.token.Authentication;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @ClassName AuthorizationSuccessEvent
 * @Description 授权成功事件
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class AuthorizationSuccessEvent extends AbstractAuthorizationEvent {

    private Authentication authentication;
    private Collection<ConfigAttribute> configAttributes;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AuthorizationSuccessEvent(Object source, Collection<ConfigAttribute> configAttributes, Authentication authentication) {
        super(source);
        if (CollectionUtils.isEmpty(configAttributes) || authentication == null){
            throw new IllegalArgumentException("构造器不接受空值参数 --> configAttributes is null or empty (or authentication is null)");
        }
        this.configAttributes = configAttributes;
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return configAttributes;
    }
}
