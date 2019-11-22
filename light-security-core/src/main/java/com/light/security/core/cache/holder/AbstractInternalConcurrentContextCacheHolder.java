package com.light.security.core.cache.holder;
import com.light.security.core.cache.context.ConcurrentContextCache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AbstractInternalConcurrentContextCacheHolder
 * @Description <code>ConcurrentHashMap</code>作为缓存容器的缓存持有者通用实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractInternalConcurrentContextCacheHolder<K, V> extends AbstractInternalContextCacheHolder implements ConcurrentContextCache<K, V> {

    protected AbstractInternalConcurrentContextCacheHolder(ConcurrentContextCache cache) {
        super(cache);
    }

    @Override
    public ConcurrentContextCache<K, V> getCache() {
        return (ConcurrentContextCache<K, V>) super.getCache();
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
    public ConcurrentHashMap<K, V> getContext() {
        return (ConcurrentHashMap<K, V>) getCache().getContext();
    }

    @Override
    public void cleanContext() {
        getCache().cleanContext();
    }

    @Override
    public int getContextCount() {
        return getCache().getContextCount();
    }
}
