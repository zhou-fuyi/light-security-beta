package com.light.security.core.access.authority;

import com.light.security.core.access.model.Authority;

/**
 * @ClassName DefaultGrantedAuthority
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-21
 */
public class DefaultGrantedAuthority extends AbstractGrantedAuthority {
    public DefaultGrantedAuthority(Authority authority) {
        super(authority);
    }
}
