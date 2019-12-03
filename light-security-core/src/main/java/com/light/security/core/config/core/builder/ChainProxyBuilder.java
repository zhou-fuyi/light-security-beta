package com.light.security.core.config.core.builder;

import com.light.security.core.config.core.AbstractConfiguredSecurityBuilder;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.SecurityBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;

/**
 * @ClassName ChainProxyBuilder
 * @Description 仿照SpringSecurity完成
 * <p>
 * The {@link ChainProxyBuilder} is created by {@link WebSecurityConfiguration} to create the
 * {@link FilterChainProxy} known as the Spring Security Filter Chain
 * (springSecurityFilterChain). The springSecurityFilterChain is the {@link Filter} that
 * the {@link DelegatingFilterProxy} delegates to.
 * </p>
 *
 * <p>
 * Customizations to the {@link ChainProxyBuilder} can be made by creating a
 * {@link com.light.security.core.config.core.WebSecurityConfigurer} or more likely by overriding
 * {@link com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter}.
 * </p>
 *
 * @see EnableWebSecurity
 * @see WebSecurityConfiguration
 *
 *
 * 翻译:
 * {@link ChainProxyBuilder}由{@link WebSecurityConfiguration}创建，以创建称为Spring Security
 * 过滤器链（springSecurityFilterChain）的{@link FilterChainProxy}。 springSecurityFilterChain
 * 是{@link DelegatingFilterProxy}委托给的{@link Filter}。
 * 可以通过创建{@link com.light.security.core.config.core.WebSecurityConfigurer}来进行{@link ChainProxyBuilder}的自定义
 * ，或者更有可能通过覆盖{@link com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter}来进行自定义。
 * @Author ZhouJian
 * @Date 2019-11-29
 */
public final class ChainProxyBuilder extends AbstractConfiguredSecurityBuilder<Filter, ChainProxyBuilder> implements SecurityBuilder<Filter>, ApplicationContextAware {

    public ChainProxyBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    protected Filter performBuild() throws Exception {
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
