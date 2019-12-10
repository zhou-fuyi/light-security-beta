package com.light.security.core.access.model;

import java.util.Collection;

/**
 * @InterfaceName Role
 * @Description 对应数据库中角色实体
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface Role {

    Collection<? extends Authority> getAuthorities();

    String getRoleName();

    Integer getKey();

    void addAuthority(Authority authority);

    void addAuthorities(Collection<? extends Authority> authorities);
}
