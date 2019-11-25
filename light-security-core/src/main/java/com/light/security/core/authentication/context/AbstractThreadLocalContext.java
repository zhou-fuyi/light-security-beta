package com.light.security.core.authentication.context;

import com.light.security.core.cache.context.AbstractContextCache;

/**
 * @ClassName AbstractThreadLocalContextCache
 * @Description 使用<code>ThreadLocal</code>作为容器实现缓存的通用实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractThreadLocalContext<T> extends AbstractContextCache implements ThreadLocalContext<T> {

    private ThreadLocal<T> threadLocalContext = new ThreadLocal<>();

    protected AbstractThreadLocalContext(){
        getCacheInstanceMap().forEach((k, v) -> {
            if (v instanceof AbstractThreadLocalContext){
                logger.info("实例全类名: {},  实例成员 -> {} <- 打印信息：{} ", k, threadLocalContext.getClass().getName(), ((AbstractThreadLocalContext) v).threadLocalContext);
            }
        });
    }

    @Override
    public void setContent(T t) {
        threadLocalContext.set(t);
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
        return threadLocalContext.get();
    }

    @Override
    public void cleanContext() {
        threadLocalContext.remove();
    }

    @Override
    public int getContextCount() {
        return threadLocalContext.get() == null ? 0 : 1;
    }
}
