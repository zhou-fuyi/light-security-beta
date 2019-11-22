package com.light.security.core.cache.holder;

import com.light.security.core.cache.Cache;
import com.light.security.core.cache.CacheHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @ClassName AbstractCacheHolder
 * @Description 缓存持有者的通用实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractCacheHolder<K, V> implements CacheHolder<K, V> {


    private final Cache cache;
    private final String supportCacheType;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractCacheHolder(Cache cache, String supportCacheType){
        if (cache == null || StringUtils.isEmpty(supportCacheType)){
            throw new IllegalArgumentException("构造器不接受空值参数 --> cache is null or (supportCacheType is null or '' )");
        }

        this.cache = cache;
        this.supportCacheType = supportCacheType;
    }

    @Override
    public Cache getCache() {
        return cache;
    }

    @Override
    public boolean support(String currentCacheType, Class<?> target) {
        if (this.supportCacheType.equals(currentCacheType)){
            return internalSupport(target);
        }
        if (logger.isWarnEnabled()){
            logger.warn("当前配置的缓存类型与框架支持类型不一致");
        }
        return false;
    }

    /**
     * 由具体实现类进行方法实现
     * @param target
     * @return
     */
    protected abstract boolean internalSupport(Class<?> target);
}
