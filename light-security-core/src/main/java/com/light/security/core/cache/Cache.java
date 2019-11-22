package com.light.security.core.cache;

/**
 * @InterfaceName Cache
 * @Description 缓存顶层接口, 缓存实现用于存储
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public interface Cache {

    /**
     * 获取缓存的名称, 虽然好像没什么用
     * @return
     */
    String getName();

}
