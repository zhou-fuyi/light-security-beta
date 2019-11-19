package com.light.security.core.context;

/**
 * @InterfaceName Context
 * @Author ZhouJian
 * @Date 2019/11/13
 * @Description 容器顶层接口
 */
public interface Context {

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
     * 获取当前容器的实际存储容量
     * @return 返回当前容器的存储数量
     */
    int getContextCount();

}
