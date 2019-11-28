package com.light.security.core.access;

import com.light.security.core.authentication.context.SecurityContext;

import java.util.Collection;

/**
 * @ClassName AuthorizationStatusToken
 * @Description 用于封装授权成功的返回对象
 * 在这里不进行runAsManager的逻辑, 所以没有对于contextHolderRefreshRequired的处理
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class AuthorizationStatusToken {

    private SecurityContext securityContext;
    private Collection<ConfigAttribute> attributes;
    private Object secureObject;

    public AuthorizationStatusToken(SecurityContext securityContext, Collection<ConfigAttribute> attributes, Object secureObject){
        this.securityContext = securityContext;
        this.attributes = attributes;
        this.secureObject = secureObject;
    }

    public Collection<ConfigAttribute> getAttributes() {
        return attributes;
    }

    public Object getSecureObject() {
        return secureObject;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

}
