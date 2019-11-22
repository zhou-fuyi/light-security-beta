package com.light.security.core.cache.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AbstractContextCache
 * @Description <code>ContextCache</code>的附加实现, 主要用于保证子类实例为单例
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractContextCache implements ContextCache{

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 用于保证存放每一个ContextCache的实例对象, 用于保证其实例对象为单例
     */
    private static final ConcurrentHashMap<String, AbstractContextCache> CACHE_INSTANCE_MAP = new ConcurrentHashMap<>();

//  Methods ==============================================================================================================================

    protected AbstractContextCache(){
        logger.info("当前实例化类为: {}", this.getClass().getName());

        getCacheInstance(this);
        logger.info("目前一共存在 {} 个子类实例对象 ", getCacheInstanceMap().size());
        //在这里不能对CACHE_INSTANCE_MAP中的value中的任何非静态成员进行访问操作, 因为value中的实例变量还没有被初始化, 会触发NPE.应该下沉到对应的初始化行为之后再进行访问
//        CACHE_INSTANCE_MAP.forEach((k, v) -> logger.info("实例全类名: {}, 实例对象打印信息：{} ", k, v.getContextCount()));
    }

    /**
     * 双检锁，控制子类实例都是单例（参考极客时间 Java核心技术36将第14讲）
     * @param instance
     * @return
     */
    private AbstractContextCache getCacheInstance(AbstractContextCache instance){
        if (CACHE_INSTANCE_MAP.get(instance.getClass().getName()) == null){
            synchronized (AbstractContextCache.class){
                if (CACHE_INSTANCE_MAP.get(instance.getClass().getName()) == null){
                    CACHE_INSTANCE_MAP.put(instance.getClass().getName(), instance);
                }
            }
        }
        return CACHE_INSTANCE_MAP.get(instance.getClass().getName());
    }

    /**
     * 返回承载子类实例的Map对象
     * @return
     */
    protected ConcurrentHashMap<String, AbstractContextCache> getCacheInstanceMap(){
        return CACHE_INSTANCE_MAP;
    }

    @Override
    public String getName() {
        if (logger.isWarnEnabled()){
            logger.warn("这里只是简单的实现了getName方法, 如有需要还请自己进行改动(此处名称不具有唯一性, 请勿使用其作为key进行一些操作)");
        }
        return this.getClass().getSimpleName();
    }
}
