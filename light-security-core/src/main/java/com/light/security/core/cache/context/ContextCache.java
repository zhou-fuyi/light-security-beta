package com.light.security.core.cache.context;

import com.light.security.core.cache.Cache;

/**
 * @InterfaceName Context
 * @Author ZhouJian
 * @Date 2019/11/13
 * @Description 自定义容器缓存顶层接口
 */
public interface ContextCache extends Cache {

    /**
     * 获取当前容器实例对象
     * @param <T>
     * @return
     */
    <T> T getContext();

    /**
     * 清空当前容器
     */
    void cleanContext();

    /**
     * 获取当前容器的实际存储容量, 计数很有可能存在误差
     * @return 返回当前容器的存储数量
     */
    int getContextCount();

}
