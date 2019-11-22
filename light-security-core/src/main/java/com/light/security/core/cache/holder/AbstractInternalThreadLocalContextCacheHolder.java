package com.light.security.core.cache.holder;

import com.light.security.core.cache.context.ThreadLocalContextCache;

/**
 * @ClassName AbstractInternalThreadLocalContextCacheHolder
 * @Description <code>ThreadLocal</code>最为缓存容器的持有者通用实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractInternalThreadLocalContextCacheHolder<T> extends AbstractInternalContextCacheHolder implements ThreadLocalContextCache<T> {

    protected AbstractInternalThreadLocalContextCacheHolder(ThreadLocalContextCache cache) {
        super(cache);
    }

    @Override
    public ThreadLocalContextCache getCache() {
        return (ThreadLocalContextCache) super.getCache();
    }

    @Override
    public T getContext() {
        return (T) getCache().getContext();
    }

    @Override
    public void setContent(T t) {
        getCache().setContent(t);
    }

    @Override
    public T createEmptyContent() {
        return (T) getCache().createEmptyContent();
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
