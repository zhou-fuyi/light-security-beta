package com.light.security.core.config.core.builder;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.config.core.SecurityBuilder;
import com.light.security.core.config.core.SecurityConfigurer;
import com.light.security.core.filter.SubjectNamePasswordAuthenticationFilter;
import com.light.security.core.filter.chain.DefaultSecurityFilterChain;

import javax.servlet.Filter;

/**
 * @InterfaceName FilterChainBuilder
 * @Description {@link FilterChainBuilder}用于构建过滤器链{@link DefaultSecurityFilterChain},
 * 这里已经限定使用的过滤器链为默认的过滤器链, 其接口为{@link com.light.security.core.filter.chain.SecurityFilterChain}
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public interface FilterChainBuilder<B extends FilterChainBuilder<B>> extends SecurityBuilder<DefaultSecurityFilterChain> {

    /**
     * 根据给定的{@link SecurityConfigurer}类名查找配置器, 如果存在, 则返回该配置器, 否则返回null
     * @param clazz
     * @param <C>
     * @return
     */
    <C extends SecurityConfigurer<DefaultSecurityFilterChain, B>> C getConfigurer(Class<C> clazz);

    /**
     * 根据给定{@link SecurityConfigurer}类名删除配置器, 如果存在, 则删除并返回该配置器, 否则返回null
     * @param clazz
     * @param <C>
     * @return
     */
    <C extends SecurityConfigurer<DefaultSecurityFilterChain, B>> C removeConfigurer(Class<C> clazz);

    /**
     * 添加共享对象
     * @param sharedType
     * @param object
     * @param <C>
     */
    <C> void setSharedObject(Class<C> sharedType, C object);

    /**
     * 根据给定的类型<code> Class<C> clazz </code>获取共享对象
     * @param clazz
     * @param <C>
     * @return
     */
    <C> C getSharedObject(Class<C> clazz);

    /**
     * 提供{@link AuthenticationProvider}以供使用
     * @param authenticationProvider
     * @return
     */
    B authenticationProvider(AuthenticationProvider authenticationProvider);

    /**
     * 提供{@link SubjectDetailService}以供使用
     * @param subjectDetailService
     * @return
     * @throws Exception
     */
    B subjectDetailService(SubjectDetailService subjectDetailService) throws Exception;

    /**
     * 允许在一个已知的{@link Filter}类之一之后添加{@link Filter}。 已知的{@link Filter}
     * 实例是{@link #addFilter（Filter）}中列出的{@link Filter}
     * 或已经使用{@link #addFilterAfter（Filter，Class）添加的{@link Filter} }
     * 或{@link #addFilterBefore（Filter，Class）}。
     *
     * @param filter
     * @param afterFilter
     * @return
     */
    B addFilterAfter(Filter filter, Class<? extends Filter> afterFilter);

    /**
     * 允许在已知的{@link Filter}类之一之前添加{@link Filter}。
     * 已知的{@link Filter}实例是{@link #addFilter（Filter）} 中列出的{@link Filter}
     * 或已经使用{@link #addFilterAfter（Filter，Class）添加的{@link Filter} }
     * 或{@link #addFilterBefore（Filter，Class）}。
     *
     * @param filter
     * @param beforeFilter
     * @return
     */
    B addFilterBefore(Filter filter, Class<? extends Filter> beforeFilter);

    /**
     * 添加过滤器, 但是这里能添加的必须是已经本框架内提供的实例或拓展
     * 该方法确保自动处理过滤器的顺序
     *
     * 提供的过滤器的顺序为：
     * <ul>
     * <li>{@link com.light.security.core.filter.SecurityContextPretreatmentFilter}</li>
     * <li>{@link SubjectNamePasswordAuthenticationFilter}</li>
     * <li>{@link com.light.security.core.filter.ExceptionTranslationFilter}</li>
     * <li>{@link com.light.security.core.filter.FilterSecurityInterceptor}</li>
     * </ul>
     * @param filter
     * @return
     */
    B addFilter(Filter filter);

}
