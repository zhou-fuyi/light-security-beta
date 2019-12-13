package com.light.security.core.config.configuration;

import com.light.security.core.cache.context.concurrent.SecurityMetadataSourceContextCache;
import com.light.security.core.cache.context.concurrent.SupportExpiredAuthenticatedContextCache;
import com.light.security.core.cache.holder.AuthenticatedContextCacheHolder;
import com.light.security.core.cache.holder.SecurityMetadataSourceContextCacheHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName CacheHolderBeanConfiguration
 * @Description <code>CacheHolder</code>Bean注册
 * @Author ZhouJian
 * @Date 2019-11-22
 */
@Configuration
public class CacheHolderBeanConfiguration {

    /**
     * 认证成功后认证数据的存储缓存注册
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticatedContextCacheHolder.class)
    public AuthenticatedContextCacheHolder authenticatedContextCacheHolder(){
        return new AuthenticatedContextCacheHolder(new SupportExpiredAuthenticatedContextCache());
    }

    @Bean
    @ConditionalOnMissingBean(SecurityMetadataSourceContextCacheHolder.class)
    public SecurityMetadataSourceContextCacheHolder securityMetadataSourceContextCacheHolder(){
        return new SecurityMetadataSourceContextCacheHolder(new SecurityMetadataSourceContextCache());
    }

}
