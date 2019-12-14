package com.light.security.core.config.core.configurer;

import com.light.security.core.access.AccessDecisionManager;
import com.light.security.core.access.AccessDecisionVoter;
import com.light.security.core.access.meta.DefaultFilterInvocationSecurityMetadataSource;
import com.light.security.core.access.meta.FilterInvocationSecurityMetadataSource;
import com.light.security.core.access.vote.AuthorityAccessDecisionManager;
import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.cache.holder.SecurityMetadataSourceContextCacheHolder;
import com.light.security.core.config.core.builder.FilterChainBuilder;
import com.light.security.core.filter.FilterSecurityInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class AbstractSecurityInterceptorConfigurer<T extends AbstractSecurityInterceptorConfigurer<T, B>, B extends FilterChainBuilder<B>>
        extends AbstractHttpConfigurer<T, B>{

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Boolean filterSecurityInterceptorOncePerRequest;

    private AccessDecisionManager accessDecisionManager;

    @Autowired
    private SecurityMetadataSourceContextCacheHolder securityMetadataSourceContextCacheHolder;//自动注入


    public SecurityMetadataSourceContextCacheHolder getSecurityMetadataSourceContextCacheHolder() {
        return securityMetadataSourceContextCacheHolder;
    }

    public void setSecurityMetadataSourceContextCacheHolder(SecurityMetadataSourceContextCacheHolder securityMetadataSourceContextCacheHolder) {
        this.securityMetadataSourceContextCacheHolder = securityMetadataSourceContextCacheHolder;
    }

    @Override
    public void configure(B builder) throws Exception {
        FilterInvocationSecurityMetadataSource securityMetadataSource = createSecurityMetadataSource(builder);
        if (securityMetadataSource == null){
            if (logger.isDebugEnabled()){
                logger.debug("securityMetadataSource 为null, 程序将不会创建FilterSecurityInterceptor");
            }
            return;
        }
        FilterSecurityInterceptor securityInterceptor = createFilterSecurityInterceptor(builder, securityMetadataSource);
        if (filterSecurityInterceptorOncePerRequest != null){
            securityInterceptor.setObserveOncePerRequest(filterSecurityInterceptorOncePerRequest);
        }
        securityInterceptor = postProcess(securityInterceptor);
        builder.addFilter(securityInterceptor);
        builder.setSharedObject(FilterSecurityInterceptor.class, securityInterceptor);
    }


    /**
     * 创建{@link FilterSecurityInterceptor}对象
     * @param builder
     * @param securityMetadataSource
     * @return
     * @throws Exception
     */
    protected FilterSecurityInterceptor createFilterSecurityInterceptor(B builder, FilterInvocationSecurityMetadataSource securityMetadataSource) throws Exception {
        FilterSecurityInterceptor securityInterceptor = new FilterSecurityInterceptor();
        securityInterceptor.setSecurityMetadataSource(securityMetadataSource);
        securityInterceptor.setAccessDecisionManager(getAccessDecisionManager(builder));
        securityInterceptor.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));
        securityInterceptor.setSecurityContextHolder(builder.getSharedObject(SecurityContextHolder.class));
        securityInterceptor.afterPropertiesSet();
        return securityInterceptor;
    }

    /**
     * 获取投票器
     * @param builder
     * @return
     */
    protected abstract List<AccessDecisionVoter<? extends Object>> getDecisionVoters(B builder);

    /**
     * 创建权限数据源, 来源于{@link com.light.security.core.cache.listener.SecurityMetadataSourceCacheListener #loadCache(ServletContextEvent)}加载
     * @param builder
     * @return
     */
    protected FilterInvocationSecurityMetadataSource createSecurityMetadataSource(B builder){
        FilterInvocationSecurityMetadataSource securityMetadataSource = new DefaultFilterInvocationSecurityMetadataSource(securityMetadataSourceContextCacheHolder);
        return securityMetadataSource;
    }

    protected AbstractSecurityInterceptorConfigurer<T, B> setAccessDecisionManager(AccessDecisionManager accessDecisionManager){
        this.accessDecisionManager = accessDecisionManager;
        return this;
    }

    protected AbstractSecurityInterceptorConfigurer<T, B> setFilterSecurityInterceptorOncePerRequest(boolean filterSecurityInterceptorOncePerRequest){
        this.filterSecurityInterceptorOncePerRequest = filterSecurityInterceptorOncePerRequest;
        return this;
    }

    protected AccessDecisionManager getAccessDecisionManager(B builder){
        if (accessDecisionManager == null){
            accessDecisionManager = new AuthorityAccessDecisionManager(getDecisionVoters(builder));
        }
        return accessDecisionManager;
    }
}
