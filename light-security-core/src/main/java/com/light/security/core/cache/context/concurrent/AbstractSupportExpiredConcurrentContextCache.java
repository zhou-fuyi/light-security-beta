package com.light.security.core.cache.context.concurrent;

import com.light.security.core.cache.model.InternalContextSupportExpired;
import com.light.security.core.cache.task.AbstractSupportExpiredCheckTask;

/**
 * @ClassName AbstractSupportExpiredConcurrentContextCache
 * @Description 使用<code>ConcurrentHashMap</code>作为容器实现缓存的通用实现, 支持过期时间设置(提供时间过期检测)
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractSupportExpiredConcurrentContextCache<K, V extends InternalContextSupportExpired> extends AbstractConcurrentContextCache<K, V> {

    protected AbstractSupportExpiredConcurrentContextCache(){
        /**
         * 使用匿名内部类的形式接受过期处理事件
         */
        AbstractSupportExpiredCheckTask<K, V> expiredCheckTask = new AbstractSupportExpiredCheckTask<K, V>(this) {
            @Override
            protected void expiredEvent(K k, V v) {
                elementExpiredEvent(k, v);
            }
        };
        expiredCheckTask.setDaemon(false);
        expiredCheckTask.start();;
        if (logger.isDebugEnabled()){
            logger.debug("AbstractSupportExpiredConcurrentContextCache 容器的过期检测线程已启动");
        }
    }

    /**
     * 本类不做处理, 将处理下沉, 由具体的实现类处理, 如果不想处理, 给出一个空实现也行
     * @param k
     * @param v
     */
    protected abstract void elementExpiredEvent(K k, V v);
}
