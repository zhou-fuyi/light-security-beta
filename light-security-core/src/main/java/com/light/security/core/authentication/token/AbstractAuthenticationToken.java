package com.light.security.core.authentication.token;

import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.CredentialsContainer;
import com.light.security.core.authentication.subject.SubjectDetail;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName AbstractAuthenticationToken
 * @Description Authentication的一些通用实现, 其中的账户认证状态的设定可以参见此类
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public abstract class AbstractAuthenticationToken implements Authentication, CredentialsContainer {

    private Collection<? extends GrantedRole> roles;
    private Object details;
    private boolean authenticated = false;//默认情况下为false, 即为未认证状态
    /**
     * 用于存储账户在认证成功后生成的token字符串, 仅仅在认证成功后{@link #authenticated=true}才能设置,
     * 用于辅助{@link com.light.security.core.authentication.context.repository.InternalSecurityContextRepository}
     * 中获取 auth_token
     */
    private Object auth;

    public AbstractAuthenticationToken(Collection<? extends GrantedRole> roles){
        if (roles == null){
            //避免集合NPE
            this.roles = Collections.emptyList();
            return;
        }
        for (GrantedRole role : roles){
            if (role == null){
                //避免集合遍历使用元素时出现NPE
                throw new IllegalArgumentException("角色集合中不能存在空元素");
            }
        }
        List<GrantedRole> targetGrantedRoles = new ArrayList<>(roles.size());
        targetGrantedRoles.addAll(roles);
        this.roles = Collections.unmodifiableList(targetGrantedRoles);//将其置为不可变集合
    }

    // ~ Methods
    // =================================================================================================================


    @Override
    public Collection<? extends GrantedRole> getGrantedRoles() {
        return roles;
    }

    @Override
    public String getName() {
        if (this.getSubject() instanceof SubjectDetail){
            return ((SubjectDetail) this.getSubject()).getSubjectName();
        }
        if (getSubject() instanceof Principal){
            return ((Principal) getSubject()).getName();
        }
        return (this.getSubject() == null) ? "" : this.getSubject().toString();
    }


    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public void eraseCredentials() {
        eraseSecret(getCredentials());//这个方法应该是一个空执行
        eraseSecret(getSubject());//将会调用SubjectDetail的实现类Subject的eraseCredentials函数
        eraseSecret(details);//todo 暂时还没有分析, 上面的分析也待验证
    }

    public void setAuth(Object auth) {
        if (!this.authenticated){
            throw new IllegalArgumentException("必须是在认证成功的情况下才能设置合法auth_token");
        }
        this.auth = auth;
    }

    @Override
    public Object getAuth() {
        return auth;
    }

    /**
     * 擦除敏感数据, 如密码
     */
    private void eraseSecret(Object secret){
        if (secret instanceof CredentialsContainer){
            ((CredentialsContainer) secret).eraseCredentials();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractAuthenticationToken)){
            return false;
        }
        AbstractAuthenticationToken test = (AbstractAuthenticationToken) obj;
        if (!roles.equals(test.roles)){
            return false;
        }
        if ((this.details == null) && (test.getDetails() != null)){
            return false;
        }
        if ((this.details != null) && (test.getDetails() == null)){
            return false;
        }
        if ((this.details != null) && (!this.details.equals(test.getDetails()))) {
            return false;
        }
        if ((this.getCredentials() == null) && (test.getCredentials() != null)) {
            return false;
        }

        if ((this.getCredentials() != null) && !this.getCredentials().equals(test.getCredentials())) {
            return false;
        }

        if (this.getSubject() == null && test.getSubject() != null) {
            return false;
        }

        if (this.getSubject() != null  && !this.getSubject().equals(test.getSubject())) {
            return false;
        }
        return this.isAuthenticated() == test.isAuthenticated();
    }

    @Override
    public int hashCode() {
        int code = 31;

        for (GrantedRole role : roles) {
            code ^= role.hashCode();
        }

        if (this.getSubject() != null) {
            code ^= this.getSubject().hashCode();
        }

        if (this.getCredentials() != null) {
            code ^= this.getCredentials().hashCode();
        }

        if (this.getDetails() != null) {
            code ^= this.getDetails().hashCode();
        }

        if (this.isAuthenticated()) {
            code ^= -37;
        }

        return code;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
