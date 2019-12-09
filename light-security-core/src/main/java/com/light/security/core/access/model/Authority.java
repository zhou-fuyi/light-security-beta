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
    boolean isOpened();

    /**
     * 获取权限类型
     * @return
     */
    String getAuthorityType();

    /**
     * 设置当前权限的父ID, 这里用于处理Lambda使用groupBy后生成HashMap的key不能为null的情况, 先行将所有为null的置为一个特殊值
     */
    void setAuthorityParentId(Integer parentId);

    /**
     * 获取当前权限的父ID
     * @return
     */
    Integer getAuthorityParentId();

    /**
     * 获取当前权限的ID
     * @return
     */
    Integer getAuthorityId();

    /**
     * 获取权限比对的值
     * @return
     */
    String getAuthorityPoint();

}
