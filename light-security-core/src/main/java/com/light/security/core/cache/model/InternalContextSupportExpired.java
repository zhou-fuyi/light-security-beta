package com.light.security.core.cache.model;

/**
 * @InterfaceName InternalContextSupportExpired
 * @Description 用于给内部缓存传递数据的数据包装类, 提供过期时间的支持
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public interface InternalContextSupportExpired <T> extends SupportExpired <T> {

    /**
     * 判断是否过期
     * @return
     */
    boolean isExpired();

}
