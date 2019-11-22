package com.light.security.core.cache.holder;

import com.light.security.core.cache.Cache;

/**
 * @ClassName AbstractRedisCacheHolder
 * @Description Redis缓存持有者通用实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractRedisCacheHolder extends AbstractCacheHolder{

    private final static String SUPPORT_CACHE_TYPE = "REDIS";


    // TODO: 2019-11-22 有待完善
    //需要写一个类进行包装redis的操作类

    /**
     *
     * @param cache
     */
    protected AbstractRedisCacheHolder(Cache cache) {
        super(cache, SUPPORT_CACHE_TYPE);
    }
}
