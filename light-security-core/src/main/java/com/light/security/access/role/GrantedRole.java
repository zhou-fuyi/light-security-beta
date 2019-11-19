package com.light.security.access.role;

import java.util.Collection;

/**
 * @InterfaceName GrantedRole
 * @Description 被授予的角色
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface GrantedRole {

    /**
     * 获取当前角色的名称
     * @return
     */
    String getGrantedRoleName();

    /**
     * 获取当前角色已经被授予的权限集合
     * @return
     */
    Collection<? extends GrantedAuthority> getGrantedAuthorities();

}
