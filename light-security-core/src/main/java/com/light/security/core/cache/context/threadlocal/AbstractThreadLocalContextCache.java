package com.light.security.core.cache.context.threadlocal;

import com.light.security.core.cache.context.AbstractContextCache;
import com.light.security.core.cache.context.ThreadLocalContextCache;

/**
 * @ClassName AbstractThreadLocalContextCache
 * @Description 使用<code>ThreadLocal</code>作为容器实现缓存的通用实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractThreadLocalContextCache<T> extends AbstractContextCache implements ThreadLocalContextCache<T> {

    private ThreadLocal<T> threadLocalContextCache = new ThreadLocal<>();

    protected AbstractThreadLocalContextCache(){
        getCacheInstanceMap().forEach((k, v) -> {
            if (v instanceof AbstractThreadLocalContextCache){
                logger.info("实例全类名: {},  实例成员 -> {} <- 打印信息：{} ", k, threadLocalContextCache.getClass().getName(), ((AbstractThreadLocalContextCache) v).threadLocalContextCache);
            }
        });
    }

    @Override
    public void setContent(T t) {
        threadLocalContextCache.set(t);
    }

    @Override
    public T createEmptyContent() {
        if (logger.isWarnEnabled()){
            logger.warn("请实现自己的 createEmptyContext 业务, 默认返回null, 可能造成NPE");
        }
        return null;
    }

    @Override
    public T getContext() {
        return threadLocalContextCache.get();
    }

    @Override
    public void cleanContext() {
        threadLocalContextCache.remove();
    }

    @Override
    public int getContextCount() {
        return threadLocalContextCache.get() == null ? 0 : 1;
    }
}
