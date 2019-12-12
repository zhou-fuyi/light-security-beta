package com.light.security.core.access.role;

import com.light.security.core.access.authority.DefaultGrantedAuthority;
import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.model.ActionAuthority;
import com.light.security.core.access.model.Authority;
import com.light.security.core.access.model.Role;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName AbstractGrantedRole
 * @Description {@link GrantedRole}的抽象实现
 * @Author ZhouJian
 * @Date 2019-12-06
 */
public abstract class AbstractGrantedRole implements GrantedRole {

    private Role role;

    public AbstractGrantedRole(Role role) {
        Assert.notNull(role,"构造器不接受空值参数 --> role is null");
        this.role = role;
    }

    @Override
    public String getGrantedRoleName() {
        return role.getRoleName();
    }

    @Override
    public Collection<GrantedAuthority> getGrantedAuthorities() {
        Collection<? extends Authority> authorities = role.getAuthorities();
        if (!CollectionUtils.isEmpty(authorities)){
            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>(authorities.size());
            for (Authority authority : authorities){
                grantedAuthorities.add(new DefaultGrantedAuthority(authority));
            }
            return grantedAuthorities;
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<GrantedAuthority> findActionAuthorities() {
        List<GrantedAuthority> actionAuthorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : getGrantedAuthorities()){
            if (grantedAuthority.getAuthority().getClass().isAssignableFrom(ActionAuthority.class)){
                actionAuthorities.add(grantedAuthority);
            }
        }
        return actionAuthorities;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
