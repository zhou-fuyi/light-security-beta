package com.light.security.core.access.model;

/**
 * @InterfaceName Authority
 * @Description 权限接口
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface Authority {

    /**
     * 当前权限是否可用
     * @return
     */
    boolean isEnabled();

    /**
     * 当前权限是否为公共资源
     * @return
     */
    boolean isOpen();



}
