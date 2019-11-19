package com.light.security.access.role;

import com.light.security.access.model.Authority;
import com.light.security.access.model.tree.Tree;

import java.io.Serializable;

/**
 * @InterfaceName GrantedAuthority
 * @Description TODO
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
