package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.config.core.SecurityConfigurer;
import com.light.security.core.config.core.builder.AuthenticationManagerBuilder;
import org.springframework.core.annotation.Order;

/**
 * @ClassName GlobalAuthenticationConfigurerAdapter
 * @Description 全局配置适配器, 是使用{@link AuthenticationManagerBuilder}构建{@link AuthenticationManager}对象
 * A {@link SecurityConfigurer} that can be exposed as a bean to configure the global
 * {@link AuthenticationManagerBuilder}. Beans of this type are automatically used by
 * {@link com.light.security.core.config.configuration.AuthenticationConfiguration} to configure the global
 * {@link AuthenticationManagerBuilder}
 *
 *
 * 翻译:
 * 一个{@link SecurityConfigurer}，可以作为Bean公开以配置全局{@link AuthenticationManagerBuilder}。
 * {@link com.light.security.core.config.configuration.AuthenticationConfiguration}自动使用这种类型的Bean来配置全局{@link AuthenticationManagerBuilder}
 * @Author ZhouJian
 * @Date 2019-12-03
 */
@Order(100)
public abstract class GlobalAuthenticationConfigurerAdapter implements SecurityConfigurer<AuthenticationManager, AuthenticationManagerBuilder> {

    @Override
    public void init(AuthenticationManagerBuilder builder) throws Exception {

    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {

    }
}
