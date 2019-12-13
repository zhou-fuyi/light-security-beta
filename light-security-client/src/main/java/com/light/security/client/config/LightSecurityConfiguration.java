package com.light.security.client.config;

import com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName LightSecurityConfiguration
 * @Description 用于注册 {@link com.light.security.core.config.core.builder.HttpSecurityBuilder}
 * {@link WebSecurityConfigurerAdapter}的默认init实现会创建一个{@link com.light.security.core.config.core.builder.HttpSecurityBuilder}对象
 * 并为{@link com.light.security.core.config.core.builder.ChainProxyBuilder}设置一个后置处理器, 用于加载授权过滤器{@link com.light.security.core.filter.FilterSecurityInterceptor}
 *
 * 也可以进行方法重写进行自定义
 * @Author ZhouJian
 * @Date 2019-12-13
 */
@Configuration
public class LightSecurityConfiguration extends WebSecurityConfigurerAdapter {
}
