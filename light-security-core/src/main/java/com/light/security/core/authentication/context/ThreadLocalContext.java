package com.light.security.core.authentication.context;

import com.light.security.core.cache.context.ContextCache;

/**
 * @InterfaceName ThreadLocalContextCache
 * @Description 使用<code>ThreadLocal</code>作为容器的顶层接口
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public interface ThreadLocalContext<T> extends ContextCache {

    /**
     * 获取ThreadLocal对象
     * @return
     */
    @Override
    T getContext();

    /**
     * 往ThreadLocal中放置一个T对象
     * @param t
     */
    void setContent(T t);

    /**
     * 创建一个空内容的T对象
     * @return
     */
    T createEmptyContent();
}
