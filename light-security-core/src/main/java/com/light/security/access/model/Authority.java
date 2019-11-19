package com.light.security.access.model;

import java.util.Collection;

/**
 * @InterfaceName Authority
 * @Description 权限接口
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface Authority {

    /**
     * 根据当前权限的父Id, 查询所关联的父亲节点
     * @param parentId
     * @return
     */
    Authority loadParent(Integer parentId);

    /**
     * 根据当前权限的Id, 查询所关联的孩子节点
     * @param id
     * @return
     */
    Collection<Authority> loadChildren(Integer id);

    /**
     * 当前权限是否可用
     * @return
     */
    boolean isEnabled();

    /**
     * 当前权限是否为公共资源
     * @return
     */
    boolean isPublic();



}
