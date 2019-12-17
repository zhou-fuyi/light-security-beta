package com.light.security.core.authentication.subject;

import com.light.security.core.access.role.GrantedRole;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * @ClassName Subject
 * @Description 默认提供的账户主体实体类
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public class Subject implements SubjectDetail {

    private final Object key;
    private final String subjectName;
    private String password;
    private final Collection<GrantedRole> roles;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    public Subject(Object key, String subjectName, String password, Collection<GrantedRole> roles){
        this(key, subjectName, password, true, true, true, true, roles);
    }

    public Subject(Object key, String subjectName, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedRole> roles){
        if (key == null || StringUtils.isEmpty(subjectName) || StringUtils.isEmpty(password)){
            throw new IllegalArgumentException("构造器不接受空值参数 --> key is null or subjectName is empty or password is empty");
        }
        this.key = key;
        this.subjectName = subjectName;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired =  credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.roles = roles;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public Collection<? extends GrantedRole> getRoles() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getSubjectName() {
        return subjectName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
