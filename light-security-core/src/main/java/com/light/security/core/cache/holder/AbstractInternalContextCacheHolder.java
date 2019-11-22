package com.light.security.core.cache.holder;

import com.light.security.core.cache.Cache;
import com.light.security.core.cache.context.ContextCache;

/**
 * @ClassName AbstractInternalContextCacheHolder
 * @Description 内部缓存持有者通过抽象实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractInternalContextCacheHolder<K, V> extends AbstractCacheHolder<K, V> {

    private final static String SUPPORT_CACHE_TYPE = "INTERNAL_CONTEXT";

    protected AbstractInternalContextCacheHolder(Cache cache) {
        super(cache, SUPPORT_CACHE_TYPE);
    }

    @Override
    public ContextCache getCache() {
        return (ContextCache) super.getCache();
    }

}
