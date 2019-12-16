package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.config.core.builder.FilterChainBuilder;
import com.light.security.core.filter.SubjectNamePasswordAuthenticationFilter;
import com.light.security.core.util.matcher.AntPathRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;

/**
 * @ClassName FormLoginConfigurer
 * @Description 表单认证配置器默认实现
 * @Author ZhouJian
 * @Date 2019-12-03
 * @param <B> 构建器
 */
public class FormLoginConfigurer<B extends FilterChainBuilder<B>> extends AbstractAuthenticationFilterConfigurer<B, FormLoginConfigurer<B>, SubjectNamePasswordAuthenticationFilter> {

    public static final String LIGHT_SECURITY_FORM_SUBJECT_NAME_KEY = "subjectName";
    public static final String LIGHT_SECURITY_FORM_PASSWORD_KEY = "password";

    public FormLoginConfigurer(SecurityContextHolder securityContextHolder){
        super(new SubjectNamePasswordAuthenticationFilter(), securityContextHolder, null);
        usernameParameter(LIGHT_SECURITY_FORM_SUBJECT_NAME_KEY);
        passwordParameter(LIGHT_SECURITY_FORM_PASSWORD_KEY);
    }

    @Override
    protected RequestMatcher createLoginProcessUrlMatcher(String loginProcessUrl) {
        return new AntPathRequestMatcher(loginProcessUrl, AbstractAuthenticationFilterConfigurer.DEFAULT_HTTP_METHOD);
    }

    public FormLoginConfigurer usernameParameter(String subjectParameter){
        getAuthFilter().setSubjectNameParameter(subjectParameter);
        return this;
    }

    public FormLoginConfigurer passwordParameter(String passwordParameter){
        getAuthFilter().setPasswordParameter(passwordParameter);
        return this;
    }

}

