package com.light.security.core.access.authority;

import com.light.security.core.access.model.Authority;
import com.light.security.core.access.model.tree.Tree;

import java.io.Serializable;

/**
 * @InterfaceName GrantedAuthority
 * @Description 被授予的权限
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface GrantedAuthority extends Tree, Serializable {

    /**
     * 获取权限
     * @return
     */
    Authority getAuthority();

    /**
     * 获取当前权限对应的类型, 常分为：action|router|element|file
     * @return
     */
    String getAuthorityType();

}
