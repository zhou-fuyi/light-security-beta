package com.light.security.core.cache.holder;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.cache.context.threadlocal.SecurityContext;

/**
 * @ClassName SecurityContextHolder
 * @Description <code>SecurityContext</code>的持有者类
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class SecurityContextHolder extends AbstractInternalThreadLocalContextCacheHolder<Authentication> {

    public SecurityContextHolder(SecurityContext cache) {
        super(cache);
    }

    @Override
    protected boolean internalSupport(Class<?> target) {
        //可以使用于接口
        return Authentication.class.isAssignableFrom(target);
    }
}
