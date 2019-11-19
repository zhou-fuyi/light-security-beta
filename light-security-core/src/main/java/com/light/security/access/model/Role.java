package com.light.security.access.model;

import java.util.Collection;

/**
 * @InterfaceName Role
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface Role {

    Collection<? extends Authority> getAuthorities();

    String getRoleName();
}
