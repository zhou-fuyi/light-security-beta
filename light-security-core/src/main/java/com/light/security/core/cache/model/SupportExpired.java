package com.light.security.core.cache.model;

import java.util.concurrent.TimeUnit;

/**
 * @InterfaceName SupportExpired
 * @Description 支持过期的数据包装接口
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public interface SupportExpired <T> {

    /**
     * 获取当前数据的key
     * @return
     */
    String getKey();

    /**
     * 获取时间的单位
     * @return
     */
    TimeUnit getTimeUnit();

    /**
     * 获取当前实例支持的过期时长
     * @return
     */
    long getExpired();

    /**
     * 在传入数据的基础上获取当前实例支持的过期时长
     * @param addition
     * @return
     */
    long getExpired(Long addition);

    /**
     * 获取实例内包裹的内容
     * @return
     */
    T getContent();

}
