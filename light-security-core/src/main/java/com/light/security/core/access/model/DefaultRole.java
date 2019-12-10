package com.light.security.core.access.model;

import java.util.Collection;

/**
 * @ClassName DefaultRole
 * @Description 默认的数据库角色实现
 * @Author ZhouJian
 * @Date 2019-12-06
 */
public class DefaultRole extends AbstractRole{

    public DefaultRole(){}

    public DefaultRole(Builder builder) {
        super(builder);
    }

    public static class Builder extends AbstractRole.Builder{

        public Builder(Integer roleId, String roleName, Collection<Authority> authorities) {
            super(roleId, roleName, authorities);
        }

        @Override
        public DefaultRole build() {
            return new DefaultRole(this);
        }
    }
}
