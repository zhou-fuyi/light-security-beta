package com.light.security.core.config.autowired;

import com.light.security.core.access.model.tree.builder.AuthorityTreeBuilder;
import com.light.security.core.access.model.tree.builder.ElementAuthorityTreeBuilder;
import com.light.security.core.access.model.tree.builder.MenuAuthorityTreeBuilder;
import com.light.security.core.access.model.tree.builder.manager.AuthorityTreeBuilderManger;
import com.light.security.core.access.model.tree.builder.manager.TreeBuilderManager;
import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.DefaultAuthenticationEventPublisher;
import com.light.security.core.authentication.context.InternalSecurityContext;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.context.repository.InternalSecurityContextRepository;
import com.light.security.core.authentication.context.repository.SecurityContextRepository;
import com.light.security.core.cache.holder.AuthenticatedContextCacheHolder;
import com.light.security.core.config.core.configurer.GlobalAuthenticationConfigurerAdapter;
import com.light.security.core.config.core.configurer.IgnoredResourcesConfigurerAdapter;
import com.light.security.core.properties.SecurityProperties;
import com.light.security.core.util.signature.InternalDefaultSignature;
import com.light.security.core.util.signature.Signature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * @ClassName LightSecurityAutoConfiguration
 * @Description 模仿SpringSecurity中 --> <code> SecurityAutoConfiguration </code>
 *
 * 算是一个全局配置器咯
 *
 * @Author ZhouJian
 * @Date 2019-12-05
 */
@Configuration
@ConditionalOnClass({AuthenticationManager.class, GlobalAuthenticationConfigurerAdapter.class})
@Import({SpringBootWebSecurityConfiguration.class, AuthenticationManagerConfiguration.class})
@EnableConfigurationProperties(SecurityProperties.class)// 开启<code> ConfigurationProperties </code>支持
public class LightSecurityAutoConfiguration {

    /**
     * 全局配置器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(SecurityProperties.class)// 仅仅在当前上下文中不存在某个类型的实例时, 才会实例化一个Bean
    public SecurityProperties securityProperties(){
        SecurityProperties securityProperties = new SecurityProperties();
        return securityProperties;
    }

    /**
     * 元素树构建器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(value = AuthorityTreeBuilder.class, name = "elementAuthorityTreeBuilder")
    public AuthorityTreeBuilder elementAuthorityTreeBuilder(){
        return new ElementAuthorityTreeBuilder();
    }

    /**
     * 菜单树构建器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(value = AuthorityTreeBuilder.class, name = "menuAuthorityTreeBuilder")
    public AuthorityTreeBuilder menuAuthorityTreeBuilder(){
        return new MenuAuthorityTreeBuilder();
    }

    /**
     * TODO 后续需要进行优化, 必须进行优化
     * 树构建器管理器
     * @param authorityTreeBuilders
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(TreeBuilderManager.class)
    public TreeBuilderManager treeBuilderManager(List<AuthorityTreeBuilder> authorityTreeBuilders){
        AuthorityTreeBuilderManger authorityTreeBuilderManger = new AuthorityTreeBuilderManger();
        authorityTreeBuilderManger.setAuthorityTreeBuilders(authorityTreeBuilders);
        authorityTreeBuilders.forEach(item -> {
            item.setTreeBuilderManager(authorityTreeBuilderManger);
        });
        return authorityTreeBuilderManger;
    }

    /**
     * SecurityContextHolder注册
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(SecurityContextHolder.class)
    public SecurityContextHolder securityContextHolder(){
        return new SecurityContextHolder(new InternalSecurityContext());
    }

    /**
     * 用于{@link com.light.security.core.filter.SecurityContextPretreatmentFilter}过滤器中
     * 加载{@link com.light.security.core.authentication.context.SecurityContext}
     * @param securityContextHolder
     * @param authenticatedContextCacheHolder
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(SecurityContextRepository.class)
    public SecurityContextRepository securityContextRepository(SecurityContextHolder securityContextHolder, AuthenticatedContextCacheHolder authenticatedContextCacheHolder){
        InternalSecurityContextRepository securityContextRepository = new InternalSecurityContextRepository();
        securityContextRepository.setAuthenticatedContextCacheHolder(authenticatedContextCacheHolder);
        securityContextRepository.setSecurityContextHolder(securityContextHolder);
        return securityContextRepository;
    }

    /**
     * 注册事件发布器
     * @param applicationEventPublisher
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ApplicationEventPublisher.class)
    public DefaultAuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher){
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }

    /**
     * 公共资源适配器注册, 必须要进行该适配器的注册, 才能使用{@link com.light.security.core.config.core.IgnoredResourcesConfigurer}接口
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IgnoredResourcesConfigurerAdapter.class)
    public IgnoredResourcesConfigurerAdapter ignoredResourcesConfigurerAdapter(){
        IgnoredResourcesConfigurerAdapter ignoredResourcesConfigurerAdapter = new IgnoredResourcesConfigurerAdapter() {

        };
        return ignoredResourcesConfigurerAdapter;
    }

}
