package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.config.core.builder.FilterChainBuilder;
import com.light.security.core.filter.UsernamePasswordAuthenticationFilter;
import com.light.security.core.util.matcher.AntPathRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;

/**
 * @ClassName FormLoginConfigurer
 * @Description 表单认证配置器默认实现
 * @Author ZhouJian
 * @Date 2019-12-03
 * @param <B> 构建器
 */
public class FormLoginConfigurer<B extends FilterChainBuilder<B>> extends AbstractAuthenticationFilterConfigurer<B, FormLoginConfigurer<B>, UsernamePasswordAuthenticationFilter> {

    public static final String LIGHT_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String LIGHT_SECURITY_FORM_PASSWORD_KEY = "password";

    public FormLoginConfigurer(SecurityContextHolder securityContextHolder){
        super(new UsernamePasswordAuthenticationFilter(), securityContextHolder, null);
        usernameParameter(LIGHT_SECURITY_FORM_USERNAME_KEY);
        passwordParameter(LIGHT_SECURITY_FORM_PASSWORD_KEY);
    }

    @Override
    protected RequestMatcher createLoginProcessUrlMatcher(String loginProcessUrl) {
        return new AntPathRequestMatcher(loginProcessUrl, AbstractAuthenticationFilterConfigurer.DEFAULT_HTTP_METHOD);
    }

    public FormLoginConfigurer usernameParameter(String usernameParameter){
        getAuthFilter().setUsernameParameter(usernameParameter);
        return this;
    }

    public FormLoginConfigurer passwordParameter(String passwordParameter){
        getAuthFilter().setPasswordParameter(passwordParameter);
        return this;
    }

}

