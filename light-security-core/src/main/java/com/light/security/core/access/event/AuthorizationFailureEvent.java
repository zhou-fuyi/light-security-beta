package com.light.security.core.access.event;

import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AccessDeniedException;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @ClassName AuthorizationFailureEvent
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class AuthorizationFailureEvent extends AbstractAuthorizationEvent {

    private AccessDeniedException accessDeniedException;
    private Authentication authentication;
    private Collection<ConfigAttribute> configAttributes;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AuthorizationFailureEvent(Object source, Collection<ConfigAttribute> configAttributes, Authentication authentication, AccessDeniedException accessDeniedException) {
        super(source);
        if (CollectionUtils.isEmpty(configAttributes) || authentication == null || accessDeniedException == null){
            throw new IllegalArgumentException("构造器不接受空值参数 --> configAttributes is null or empty (or authentication is null) or (accessDeniedException is null)");
        }
        this.configAttributes = configAttributes;
        this.authentication = authentication;
        this.accessDeniedException = accessDeniedException;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return configAttributes;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public AccessDeniedException getAccessDeniedException() {
        return accessDeniedException;
    }
}
