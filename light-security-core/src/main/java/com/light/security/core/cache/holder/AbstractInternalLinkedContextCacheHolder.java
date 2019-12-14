package com.light.security.core.cache.holder;

import com.light.security.core.cache.Cache;
import com.light.security.core.cache.context.ConcurrentContextCache;
import com.light.security.core.cache.context.LinkedContextCache;

/**
 * @ClassName AbstractInternalLinkedContextCacheHolder
 * @Description <code>LinkedHashMap</code>作为缓存容器的缓存持有者通用实现
 * @Author ZhouJian
 * @Date 2019-12-14
 */
public abstract class AbstractInternalLinkedContextCacheHolder<K, V> extends AbstractInternalContextCacheHolder<K, V> {

    protected AbstractInternalLinkedContextCacheHolder(Cache cache) {
        super(cache);
    }

    @Override
    public LinkedContextCache<K, V> getCache() {
        return (LinkedContextCache<K, V>) super.getCache();
    }

    @Override
    public void put(K k, V v) {
        getCache().put(k, v);
    }

    @Override
    public V get(K k) {
        return (V) getCache().get(k);//应该是类型擦除了
    }

    @Override
    public void remove(K k) {
        getCache().remove(k);
    }

    @Override
    public void clearCache() {
        getCache().cleanContext();
    }
}
