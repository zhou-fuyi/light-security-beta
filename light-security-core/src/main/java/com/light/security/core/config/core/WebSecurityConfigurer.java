package com.light.security.core.config.core;

import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @InterfaceName WebSecurityConfigurer
 * @Description 仿照SpringSecurity完成
 * Allows customization to the {@link WebSecurity}. In most instances users will use
 * {@link EnableWebSecurity} and a create {@link Configuration} that extends
 * {@link WebSecurityConfigurerAdapter} which will automatically be applied to the
 * {@link WebSecurity} by the {@link EnableWebSecurity} annotation.
 *
 *
 * 翻译:
 * 允许自定义{@link WebSecurity}。 在大多数情况下，用户将使用{@link EnableWebSecurity}
 * 和扩展{@link WebSecurityConfigurerAdapter}的创建{@link Configuration}，
 * 该配置将通过{@link EnableWebSecurity}注释自动应用于{@link WebSecurity}。
 * @Author ZhouJian
 * @Date 2019-11-29
 *
 * 小声BB:
 * 该接口只承接Web安全方面的配置, 并且构建目前对象类型为 {@link Filter}, 所以其子类只需要传递构建器类型 {@link B}
 */
public interface WebSecurityConfigurer<B extends SecurityBuilder<Filter>> extends SecurityConfigurer<Filter, B> {
}
