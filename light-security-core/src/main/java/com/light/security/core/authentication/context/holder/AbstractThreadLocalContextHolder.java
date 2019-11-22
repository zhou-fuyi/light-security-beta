package com.light.security.core.authentication.context.holder;

import com.light.security.core.authentication.context.ThreadLocalContext;
import org.springframework.util.Assert;

/**
 * @ClassName AbstractInternalThreadLocalContextCacheHolder
 * @Description <code>ThreadLocal</code>作为容器的容器持有者通用实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractThreadLocalContextHolder<T> implements ThreadLocalContext<T> {

    private final ThreadLocalContext strategy;

    protected AbstractThreadLocalContextHolder(ThreadLocalContext strategy) {
        Assert.notNull(strategy, "构造器不接受空值参数");
        this.strategy = strategy;
    }

    @Override
    public String getName() {
        return getStrategy().getName();
    }

    public ThreadLocalContext getStrategy() {
        return strategy;
    }


    @Override
    public T getContext() {
        return (T) getStrategy().getContext();
    }

    @Override
    public void setContent(T t) {
        getStrategy().setContent(t);
    }

    @Override
    public T createEmptyContent() {
        return (T) getStrategy().createEmptyContent();
    }

    @Override
    public void cleanContext() {
        getStrategy().cleanContext();
    }

    @Override
    public int getContextCount() {
        return getStrategy().getContextCount();
    }
}
