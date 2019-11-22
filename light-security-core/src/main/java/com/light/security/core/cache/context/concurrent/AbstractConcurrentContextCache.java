package com.light.security.core.cache.context.concurrent;

import com.light.security.core.cache.context.AbstractContextCache;
import com.light.security.core.cache.context.ConcurrentContextCache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AbstractConcurrentContextCache
 * @Description 使用<code>ConcurrentHashMap</code>作为容器实现缓存的通用实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractConcurrentContextCache<K, V> extends AbstractContextCache implements ConcurrentContextCache<K, V> {

    //容器的真正实例
    private ConcurrentHashMap<K, V> concurrentCache = new ConcurrentHashMap<>();

    protected AbstractConcurrentContextCache(){
        getCacheInstanceMap().forEach((k, v) -> {
            if (v instanceof AbstractConcurrentContextCache){
                logger.info("实例全类名: {}, 实例成员 -> {} <- 打印信息：{} ", k, concurrentCache.getClass().getName(), ((AbstractConcurrentContextCache) v).concurrentCache);
            }
        });
    }

    @Override
    public void put(K k, V v) {
        concurrentCache.put(k, v);
    }

    @Override
    public V get(K k) {
        return concurrentCache.get(k);
    }

    @Override
    public void remove(K k) {
        concurrentCache.remove(k);
    }

    @Override
    public void cleanContext() {
        concurrentCache.clear();
    }

    @Override
    public int getContextCount() {
        return concurrentCache.size();
    }

    @Override
    public ConcurrentHashMap<K, V> getContext() {
        return concurrentCache;
    }
}
