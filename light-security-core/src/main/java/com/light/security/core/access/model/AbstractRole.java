package com.light.security.core.access.model;

import com.light.security.core.access.model.base.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * @ClassName AbstractRole
 * @Description 角色实体抽象实现
 * @Author ZhouJian
 * @Date 2019-12-06
 */
public abstract class AbstractRole extends BaseEntity implements Role {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String roleName;
    private String roleCode;
    private String roleDesc;
    private Collection<Authority> authorities;

    protected AbstractRole(){}

    public AbstractRole (Builder builder){
        this.id = builder.getId();
        this.roleName = builder.roleName;
        this.roleCode = builder.roleCode;
        this.roleDesc = builder.roleDesc;
        this.authorities = builder.authorities;
        this.createTime = builder.getCreateTime();
        this.updateTime = builder.getUpdateTime();
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    @Override
    public Collection<? extends Authority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    @Override
    public Integer getKey() {
        return this.id;
    }

    @Override
    public void addAuthority(Authority authority) {
        this.authorities.add(authority);
    }

    @Override
    public void addAuthorities(Collection<Authority> authorities) {
        this.authorities.addAll(authorities);
    }

    @Override
    public void setAuthorities(Collection<Authority> authorities) {
        this.authorities = authorities;
    }

    public abstract static class Builder extends BaseEntity.AbstractBaseBuilder{

        private String roleName;
        private Collection<Authority> authorities;

        private String roleCode;
        private String roleDesc;

        public Builder(final Integer id, String roleName, Collection<Authority> authorities){
            super(id);
            Assert.isTrue((!StringUtils.isEmpty(roleName)  && !CollectionUtils.isEmpty(authorities)),
                    "构造器不接受空值参数 --> roleName is Empty or authorities is empty collection");
            this.roleName = roleName;
            this.authorities = authorities;
        }

        public Builder roleCode(String roleCode){
            this.roleCode = roleCode;
            return this;
        }

        public Builder roleDesc(String roleDesc){
            this.roleDesc = roleDesc;
            return this;
        }

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
