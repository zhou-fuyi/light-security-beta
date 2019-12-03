package com.light.security.core.config.core.configurer;

import com.light.security.core.access.handler.AccessDeniedHandler;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.entrypoint.AuthenticationEntryPoint;
import com.light.security.core.authentication.entrypoint.DefaultAuthenticationEntryPoint;
import com.light.security.core.authentication.entrypoint.DelegatingAuthenticationEntryPoint;
import com.light.security.core.config.core.builder.FilterChainBuilder;
import com.light.security.core.filter.ExceptionTranslationFilter;
import com.light.security.core.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;

/**
 * @ClassName ExceptionTranslationConfigurer
 * @Description {@link com.light.security.core.filter.ExceptionTranslationFilter}过滤器配置器
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class ExceptionTranslationConfigurer<B extends FilterChainBuilder<B>> extends AbstractHttpConfigurer<ExceptionTranslationConfigurer<B>, B> {

    private SecurityContextHolder securityContextHolder;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private AccessDeniedHandler accessDeniedHandler;

    private LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> defaultEntryPointMappings = new LinkedHashMap<>();

    public ExceptionTranslationConfigurer(SecurityContextHolder securityContextHolder){
        Assert.notNull(securityContextHolder, "构造器不接受空值参数 --> securityContextHolder is null");
        this.securityContextHolder = securityContextHolder;
    }

    public ExceptionTranslationConfigurer<B> authenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint){
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this;
    }

    public ExceptionTranslationConfigurer<B> accessDeniedHandler(AccessDeniedHandler accessDeniedHandler){
        this.accessDeniedHandler = accessDeniedHandler;
        return this;
    }

    /**
     * Sets a default {@link AuthenticationEntryPoint} to be used which prefers being
     * invoked for the provided {@link RequestMatcher}. If only a single default
     * {@link AuthenticationEntryPoint} is specified, it will be what is used for the
     * default {@link AuthenticationEntryPoint}. If multiple default
     * {@link AuthenticationEntryPoint} instances are configured, then a
     * {@link com.light.security.core.authentication.entrypoint.DelegatingAuthenticationEntryPoint} will be used.
     *
     *
     * 设置要使用的默认{@link AuthenticationEntryPoint}，它优先为提供的{@link RequestMatcher}调用。
     * 如果仅指定了一个默认的{@link AuthenticationEntryPoint}，它将用于默认的{@link AuthenticationEntryPoint}。
     * 如果配置了多个默认{@link AuthenticationEntryPoint}实例，则将使用{@link com.light.security.core.authentication.entrypoint.DelegatingAuthenticationEntryPoint}。
     * @param entryPoint
     * @param preferredMatcher 首选匹配器
     * @return
     */
    public ExceptionTranslationConfigurer<B> defaultAuthenticationEntryPointFor(AuthenticationEntryPoint entryPoint, RequestMatcher preferredMatcher){
        this.defaultEntryPointMappings.put(preferredMatcher, entryPoint);
        return this;
    }

    public SecurityContextHolder getSecurityContextHolder() {
        return securityContextHolder;
    }

    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    public AuthenticationEntryPoint getAuthenticationEntryPoint(B builder) {
        if (authenticationEntryPoint == null){
            return createDefaultEntryPoint(builder);
        }
        return authenticationEntryPoint;
    }

    public AccessDeniedHandler getAccessDeniedHandler() {
        return accessDeniedHandler;
    }

    @Override
    public void configure(B builder) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint = getAuthenticationEntryPoint(builder);
        ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter(authenticationEntryPoint, securityContextHolder);
        if (accessDeniedHandler != null){
            exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandler);
        }
        exceptionTranslationFilter = postProcess(exceptionTranslationFilter);
        builder.addFilter(exceptionTranslationFilter);
    }

    /**
     * 根据情况获取{@link AuthenticationEntryPoint}对象
     * @param builder
     * @return
     */
    private AuthenticationEntryPoint createDefaultEntryPoint(B builder) {
        if (defaultEntryPointMappings.isEmpty()) {
            return new DefaultAuthenticationEntryPoint();
        }
        if (defaultEntryPointMappings.size() == 1) {
            return defaultEntryPointMappings.values().iterator().next();
        }
        DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(
                defaultEntryPointMappings);
        entryPoint.setDefaultEntryPoint(defaultEntryPointMappings.values().iterator()
                .next());
        return entryPoint;
    }
}
