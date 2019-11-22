package com.light.security.core.authentication.context.holder;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.authentication.context.SecurityContext;

/**
 * @ClassName SecurityContextHolder
 * @Description <code>SecurityContext</code>的持有者类
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class SecurityContextHolder extends AbstractThreadLocalContextHolder<Authentication> {

    public SecurityContextHolder(SecurityContext cache) {
        super(cache);
    }

}
