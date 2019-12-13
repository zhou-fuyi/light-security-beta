package com.light.security.core.cache.listener;

import com.light.security.core.access.AuthorityAttribute;
import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.meta.DefaultFilterInvocationSecurityMetadataSource;
import com.light.security.core.access.model.ActionAuthority;
import com.light.security.core.access.model.Authority;
import com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessor;
import com.light.security.core.authentication.dao.jdbc.JdbcQuery;
import com.light.security.core.cache.holder.SecurityMetadataSourceContextCacheHolder;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.SecurityBuilder;
import com.light.security.core.config.core.WebSecurityConfigurer;
import com.light.security.core.config.core.builder.ChainProxyBuilder;
import com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter;
import com.light.security.core.exception.ProcessorNotFoundException;
import com.light.security.core.properties.SecurityProperties;
import com.light.security.core.util.matcher.AntPathRequestMatcher;
import com.light.security.core.util.matcher.OrRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.util.*;

/**
 * @ClassName SecurityMetadataSourceCacheListener
 * @Description 用于加载<code>SecurityMetadataSource</code>, 并将其存放于<code>SecurityMetadataSourceCache</code>中
 * @Author ZhouJian
 * @Date 2019-11-28
 */
@WebListener
public class SecurityMetadataSourceCacheListener extends AbstractCacheContextListener {

    private final double AUTHORITY_OPENED_THRESHOLD = 0.5d;

    @Autowired
    private SecurityMetadataSourceContextCacheHolder securityMetadataSourceContextCacheHolder;

    @Autowired
    private List<JdbcDaoProcessor> jdbcDaoProcessors;

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 依赖查找时加载, 避免使用时未初始化导致SQL为空异常
     */
    @Autowired
    private JdbcQuery jdbcQuery;

    @Autowired
    private ObjectPostProcessor<Object> postProcessor;

    @Autowired
    private ConfigurableBeanFactory beanFactory;

    @Override
    protected void loadCache(ServletContextEvent servletContextEvent) throws Exception {
        Enum currentSecurityMode = securityProperties.getAuthType();
        Collection<Authority> securityMetadata = null;
        Exception lastException = null;
        for (JdbcDaoProcessor processor : jdbcDaoProcessors){
            if (processor.support(currentSecurityMode)){
                try {
                    securityMetadata = processor.loadSecurityMetadataOnStartup();
                } catch (Exception e) {
                    lastException = e;
                }
                break;
            }
        }
        if (!CollectionUtils.isEmpty(securityMetadata)){
            transformToSecurityMetadataSources(securityMetadata);
            return;
        }
        if (lastException == null){
            lastException = new ProcessorNotFoundException(500, "未找到适配当前权限模式(" + currentSecurityMode.name() + ")的 JdbcDaoProcessor处理器");
        }
        throw lastException;
    }

    private void transformToSecurityMetadataSources(Collection<Authority> authorities){
        authorities = preHandler(authorities);
        if (!CollectionUtils.isEmpty(authorities)){
            authorities.forEach(authority -> {
                securityMetadataSourceContextCacheHolder.put(getRequestMatcher(authority), new ArrayList<>(Arrays.asList(new AuthorityAttribute(authority.getAuthorityPoint()))));
            });
        }
        logger.info("SecurityMetadataContext is {}",securityMetadataSourceContextCacheHolder.getCache().getContext());
        securityMetadataSourceContextCacheHolder.getCache().getContext().forEach((key, value) -> {
            logger.debug("key is {} and the value is {}", key, value.iterator().next());
        });
    }

    private Collection<Authority> preHandler(Collection<Authority> authorities){
        Collection<Authority> openedAuthorities = new ArrayList<>();
        final int authoritiesSize = authorities.size();
        Iterator<Authority> iterator = authorities.iterator();
        while (iterator.hasNext()){
            Authority authority = iterator.next();
            if (authority.isEnabled() && authority.isOpened()){
                openedAuthorities.add(authority);
                iterator.remove();
            }
            /**
             * 如果权限不可用, 那就去除
             */
            if (!authority.isEnabled()){
                iterator.remove();
            }
        }
        ignoredOpenedAuthoritiesSecurityConfigurerAdapter(openedAuthorities);
        if (CollectionUtils.isEmpty(authorities)){
            throw new IllegalArgumentException("当前已无可用权限数据, 请检查数据库中权限表数据列 enabled 与 opened 是否设置合适");
        }
        preCheck(authoritiesSize, openedAuthorities.size());
        return authorities;
    }

    private void preCheck(int authoritiesSize, int openedAuthoritiesSize){
        if (openedAuthoritiesSize/ (authoritiesSize * 1.0) >= AUTHORITY_OPENED_THRESHOLD){
            if (logger.isWarnEnabled()){
                logger.warn("当前获取权限数据共有{}则, 开放权限共有{}, 开放权限占比为{}则, 请注意是否开放权限设置过多"
                        , authoritiesSize, openedAuthoritiesSize, (openedAuthoritiesSize/ (authoritiesSize * 1.0)));
            }
        }
    }

    private RequestMatcher getRequestMatcher(Authority authority){
        ActionAuthority actionAuthority = (ActionAuthority) authority;
        RequestMatcher requestMatcher = new AntPathRequestMatcher(actionAuthority.getPattern());
        return requestMatcher;
    }

    /**
     * 将开放权限注册到{@link ChainProxyBuilder #ignoredRequests}中
     * @param openedAuthorities
     */
    private void ignoredOpenedAuthoritiesSecurityConfigurerAdapter(Collection<Authority> openedAuthorities){

        if (!CollectionUtils.isEmpty(openedAuthorities)){
            WebSecurityConfigurer ignoredOpenedAuthoritiesSecurityConfigurerAdapter = new WebSecurityConfigurer<ChainProxyBuilder>() {

                @Override
                public void init(ChainProxyBuilder builder) throws Exception {
                    if (logger.isDebugEnabled()){
                        logger.debug("当前开放API权限共有{}则", openedAuthorities.size());
                        openedAuthorities.forEach(openedAuthority -> logger.debug("开放API权限: {}", openedAuthority));
                    }
                }

                @Override
                public void configure(ChainProxyBuilder builder) throws Exception {
                    if (!CollectionUtils.isEmpty(openedAuthorities)){
                        List<RequestMatcher> ignoredMatchers = new ArrayList<>(openedAuthorities.size());
                        for (Authority openedAuthority : openedAuthorities){
                            ignoredMatchers.add(new AntPathRequestMatcher(((ActionAuthority) openedAuthority).getPattern()));
                        }
                        if (!ignoredMatchers.isEmpty()){
                            builder.ignoredRequestRegistry().requestMatchers(new OrRequestMatcher(ignoredMatchers));
                        }
                    }
                }
            };
            beanFactory.registerSingleton(ignoredOpenedAuthoritiesSecurityConfigurerAdapter.getClass().getName(), ignoredOpenedAuthoritiesSecurityConfigurerAdapter);
        }
    }
}
