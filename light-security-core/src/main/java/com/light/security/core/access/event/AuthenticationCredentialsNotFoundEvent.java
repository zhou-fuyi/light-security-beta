package com.light.security.core.access.event;

import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.exception.AuthenticationCredentialsNotFoundException;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @ClassName AuthenticationCredentialsNotFoundEvent
 * @Description 表示安全对象调用失败，因为无法从<code> SecurityContextHolder </ code>获取<code> Authentication </ code>。
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class AuthenticationCredentialsNotFoundEvent extends AbstractAuthorizationEvent {

    private AuthenticationCredentialsNotFoundException credentialsNotFoundException;
    private Collection<ConfigAttribute> configAttributes;

    public AuthenticationCredentialsNotFoundEvent(Object sourceObject, Collection<ConfigAttribute> configAttributes, AuthenticationCredentialsNotFoundException credentialsNotFoundException){
        super(sourceObject);
        if (CollectionUtils.isEmpty(configAttributes) || credentialsNotFoundException == null){
            throw new IllegalArgumentException("构造器不接受空值参数 --> configAttributes is null or empty (or credentialsNotFoundException is null)");
        }
        this.configAttributes = configAttributes;
        this.credentialsNotFoundException = credentialsNotFoundException;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return configAttributes;
    }

    public AuthenticationCredentialsNotFoundException getCredentialsNotFoundException() {
        return credentialsNotFoundException;
    }
}
