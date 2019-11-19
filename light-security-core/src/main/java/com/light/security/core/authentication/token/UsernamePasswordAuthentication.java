package com.light.security.core.authentication.token;

import com.light.security.core.access.role.GrantedRole;

import java.util.Collection;

/**
 * @ClassName UsernamePasswordAuthentication
 * @Description 账户密码登录时数据承载对象
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class UsernamePasswordAuthentication extends AbstractAuthenticationToken {

    private final Object subject;
    private Object credentials;

    public UsernamePasswordAuthentication(Object subject, Object credentials){
        super(null);
        this.subject = subject;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public UsernamePasswordAuthentication(Object subject, Object credentials, Collection<? extends GrantedRole> roles) {
        super(roles);
        this.subject = subject;
        this.credentials = credentials;
        super.setAuthenticated(true);//是在这里设置的账户的认证状态, 需要经过这个构造函数才能常规的指定的账户的认证状态
    }

    @Override
    public Object getSubject() {
        return this.subject;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated){
            throw new IllegalArgumentException("无法将此令牌设置为已认证状态, 需要使用带有GrantedRole集合形参的构造函数代替");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials =  null;//强制将credentials置为null
    }
}
