package com.light.security.core.cache.task;

import com.light.security.core.cache.context.concurrent.AbstractSupportExpiredConcurrentContextCache;
import com.light.security.core.cache.model.InternalContextSupportExpired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AbstractSupportExpiredCheckTask
 * @Description 进行过期时间检测的通用抽象类, 用于内部实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractSupportExpiredCheckTask<K, V extends InternalContextSupportExpired> extends Thread {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractSupportExpiredCheckTask.class);
    private static final long DEFAULT_SLEEP_TIME = 2000;//心跳间隔时长, 这里单位默认为毫秒

    private AbstractSupportExpiredConcurrentContextCache<K, V> expiredConcurrentContextCache =  null;

    protected AbstractSupportExpiredCheckTask(AbstractSupportExpiredConcurrentContextCache<K, V> expiredConcurrentContextCache){
        Assert.notNull(expiredConcurrentContextCache, "构造器不接受空值参数 --> expiredConcurrentContextCache is null");
        logger.info("已初始化 AbstractSupportExpiredConcurrentContextCache 容器的过期检测线程");
        this.expiredConcurrentContextCache = expiredConcurrentContextCache;
    }

    /**
     * 过期处理事件, 可以实现关联数据的移除
     */
    protected abstract void expiredEvent(K k, V v);

    @Override
    public void run() {
        Iterator<K> iterator = null;
        K key = null;
        while (true){
            if (expiredConcurrentContextCache.getContext() != null){
                synchronized (expiredConcurrentContextCache){
                    if (expiredConcurrentContextCache.getContext() != null){
                        iterator = expiredConcurrentContextCache.getContext().keySet().iterator();
                        while (iterator.hasNext()){
                            key = iterator.next();
                            if (expiredConcurrentContextCache.get(key).isExpired()){
                                expiredEvent(key, expiredConcurrentContextCache.get(key));
                                iterator.remove();//将迭代器中的key移除
                                expiredConcurrentContextCache.remove(key);
                                if (logger.isDebugEnabled()){
                                    logger.debug("key is {} 的数据已过期, 已经被移除", key);
                                }
                            }
                        }
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(DEFAULT_SLEEP_TIME);
                    }catch (InterruptedException e){
                        logger.error("过期时间检测线程异常: {}", e.getMessage());
                    }
                }
            }
        }
    }
}
