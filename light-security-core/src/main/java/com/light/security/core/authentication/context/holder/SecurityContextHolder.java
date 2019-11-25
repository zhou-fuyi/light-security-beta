package com.light.security.core.authentication.context.holder;

import com.light.security.core.authentication.context.SecurityContext;
import com.light.security.core.authentication.context.InternalSecurityContext;

/**
 * @ClassName SecurityContextHolder
 * @Description <code>SecurityContext</code>的持有者类
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class SecurityContextHolder extends AbstractThreadLocalContextHolder<SecurityContext> {

    public SecurityContextHolder(InternalSecurityContext cache) {
        super(cache);
    }

}
