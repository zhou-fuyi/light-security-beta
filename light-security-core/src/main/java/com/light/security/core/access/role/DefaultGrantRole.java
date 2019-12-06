package com.light.security.core.access.role;

import com.light.security.core.access.model.Role;

/**
 * @ClassName DefaultGrantRole
 * @Description 默认的已授权角色实现
 * @Author ZhouJian
 * @Date 2019-12-06
 */
public class DefaultGrantRole extends AbstractGrantedRole {
    public DefaultGrantRole(Role role) {
        super(role);
    }
}
