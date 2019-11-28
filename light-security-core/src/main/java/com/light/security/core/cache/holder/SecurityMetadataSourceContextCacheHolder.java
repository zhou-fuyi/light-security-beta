package com.light.security.core.cache.holder;

import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.cache.context.concurrent.SecurityMetadataSourceContextCache;
import com.light.security.core.util.matcher.RequestMatcher;

import java.util.Collection;

/**
 * @ClassName SecurityMetadataSourceContextCacheHolder
 * @Description 用于操作<code>SecurityMetadataSource</code>
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class SecurityMetadataSourceContextCacheHolder extends AbstractInternalConcurrentContextCacheHolder<RequestMatcher, Collection<ConfigAttribute>> {

    public SecurityMetadataSourceContextCacheHolder(SecurityMetadataSourceContextCache cache) {
        super(cache);
    }

    @Override
    protected boolean internalSupport(Class<?> target) {
        return ConfigAttribute.class.isAssignableFrom(target);
    }
}
