package com.light.security.core.cache.model;

/**
 * @InterfaceName RedisSupportExpired
 * @Description 用于给Redis传递数据的包装类, 主要是为了统一与内部缓存实现, 所以才进行包装, 提供过期时间的支持
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public interface RedisSupportExpired<T> extends SupportExpired<T>{

    /**
     * 获取用于进行读写的数据类型, 可能是: String | List | HashSet 等
     * @return
     */
    String getDataType();
}
