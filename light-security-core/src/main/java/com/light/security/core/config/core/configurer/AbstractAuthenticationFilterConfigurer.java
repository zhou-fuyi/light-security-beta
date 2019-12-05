package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.AuthenticationDetailsSource;
import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.entrypoint.AuthenticationEntryPoint;
import com.light.security.core.authentication.entrypoint.DefaultAuthenticationEntryPoint;
import com.light.security.core.authentication.handler.AuthenticationFailureHandler;
import com.light.security.core.authentication.handler.AuthenticationSuccessHandler;
import com.light.security.core.authentication.handler.SimpleAppAuthenticationFailureHandler;
import com.light.security.core.authentication.handler.SimpleAppAuthenticationSuccessHandler;
import com.light.security.core.config.core.builder.FilterChainBuilder;
import com.light.security.core.filter.AbstractAuthenticationFilter;
import com.light.security.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName AbstractAuthenticationFilterConfigurer
 * @Description 表单认证配置器抽象类, 用于配置表单认证过滤器 --> {@link com.light.security.core.filter.UsernamePasswordAuthenticationFilter}
 *
 * 注意, 这只是一个抽象类
 * @Author ZhouJian
 * @Date 2019-12-03
 * @param <B> 构建器
 * @param <T> 配置器
 * @param <F> 表单认证过滤器
 */
public abstract class AbstractAuthenticationFilterConfigurer<B extends FilterChainBuilder<B>,
        T extends AbstractAuthenticationFilterConfigurer<B, T, F>, F extends AbstractAuthenticationFilter> extends AbstractHttpConfigurer<T, B>{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static final String DEFAULT_PROCESS_URL = "/login";
    protected static final String DEFAULT_HTTP_METHOD = "POST";

    /**
     * 用于认证的过滤器, 这里的实例对象是{@link com.light.security.core.filter.UsernamePasswordAuthenticationFilter}
     */
    private F authFilter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    private AuthenticationSuccessHandler successHandler = new SimpleAppAuthenticationSuccessHandler();

    private AuthenticationEntryPoint authenticationEntryPoint = new DefaultAuthenticationEntryPoint();

    private String loginProcessUrl;

    private AuthenticationFailureHandler failureHandler = new SimpleAppAuthenticationFailureHandler();

    private SecurityContextHolder securityContextHolder;

    private ApplicationEventPublisher eventPublisher;

    protected AbstractAuthenticationFilterConfigurer(F authFilter, SecurityContextHolder securityContextHolder, String defaultLoginProcessUrl){
        Assert.isTrue((authFilter != null && securityContextHolder != null), "构造器不接受空值参数 --> authFilter is null or securityContextHolder is null");
        this.authFilter = authFilter;
        this.securityContextHolder = securityContextHolder;
        if (!StringUtils.isEmpty(defaultLoginProcessUrl)){
            loginProcessUrl(defaultLoginProcessUrl);
        }
    }

    public T loginProcessUrl(String loginProcessUrl){
        this.loginProcessUrl = loginProcessUrl;
        this.authFilter.setMatcher(createLoginProcessUrlMatcher(loginProcessUrl));
        return getSelf();
    }

    /**
     * 设置 AuthenticationDetailsSource
     * @param authenticationDetailsSource
     * @return
     */
    public final T authenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource){
        this.authenticationDetailsSource = authenticationDetailsSource;
        return getSelf();
    }

    /**
     * 设置失败处理器 AuthenticationFailureHandler
     * @param authenticationFailureHandler
     * @return
     */
    public final T failureHandler(AuthenticationFailureHandler authenticationFailureHandler){
        this.failureHandler = authenticationFailureHandler;
        return getSelf();
    }

    /**
     * 设置成功处理器 AuthenticationSuccessHandler
     * @param authenticationSuccessHandler
     * @return
     */
    public final T successHandler(AuthenticationSuccessHandler authenticationSuccessHandler){
        this.successHandler = successHandler;
        return getSelf();
    }

    /**
     * 设置 AuthenticationEntryPoint
     * @param authenticationEntryPoint
     * @return
     */
    public final T authenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint){
        this.authenticationEntryPoint = authenticationEntryPoint;
        return getSelf();
    }

    public final T eventPublisher(ApplicationEventPublisher eventPublisher){
        this.eventPublisher = eventPublisher;
        return getSelf();
    }

    protected final F getAuthFilter(){
        return this.authFilter;
    }

    /**
     * 由子类来实现匹配器
     * @param loginProcessUrl
     * @return
     */
    protected abstract RequestMatcher createLoginProcessUrlMatcher(String loginProcessUrl);

    @Override
    public void init(B builder) throws Exception {
        updateAuthenticationDefaults();
    }

    @Override
    public void configure(B builder) throws Exception {
        authFilter.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));
        authFilter.setSuccessHandler(successHandler);
        authFilter.setFailureHandler(failureHandler);
        if (authenticationDetailsSource != null){
            authFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
        }
        authFilter.setSecurityContextHolder(securityContextHolder);
        if (eventPublisher != null){
            authFilter.setEventPublisher(eventPublisher);
        }
        F filter = postProcess(authFilter);
        builder.addFilter(filter);
    }

    /**
     * 进行一些默认定义
     */
    private void updateAuthenticationDefaults(){
        if (loginProcessUrl == null){
            loginProcessUrl(DEFAULT_PROCESS_URL);
        }
    }

    private T getSelf(){
        return (T) this;
    }
}
