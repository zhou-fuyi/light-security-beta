package com.light.security.core.cache;

/**
 * @InterfaceName CacheHolder
 * @Description 缓存的持有者, 用于包装<code>Cache</code>, 避免进行直接操作. <code>CacheManager</code>封装了对缓存的常用操作
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public interface CacheHolder {

    Cache getCache();

    /**
     * 判断当前缓存持有者是否支持环境中的缓存类型与缓存数据对象
     * @param currentCacheType
     * @param target
     * @return
     */
    boolean support(String currentCacheType, Class<?> target);

}
