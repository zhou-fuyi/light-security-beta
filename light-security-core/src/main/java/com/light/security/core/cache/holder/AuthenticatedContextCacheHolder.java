package com.light.security.core.cache.holder;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.cache.context.ConcurrentContextCache;

/**
 * @ClassName AuthenticatedContextCacheHolder
 * @Description 认证成功后数据缓存容器的持有者
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class AuthenticatedContextCacheHolder extends AbstractInternalConcurrentContextCacheHolder<String, Authentication> {


    public AuthenticatedContextCacheHolder(ConcurrentContextCache cache) {
        super(cache);
    }

    @Override
    protected boolean internalSupport(Class<?> target) {
        return Authentication.class.isAssignableFrom(target);
    }
}
