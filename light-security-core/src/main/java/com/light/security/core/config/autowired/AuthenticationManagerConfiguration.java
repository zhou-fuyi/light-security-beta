package com.light.security.core.config.autowired;

import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.config.configuration.AuthenticationConfiguration;
import com.light.security.core.config.core.ObjectPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

/**
 * @ClassName AuthenticationManagerConfiguration
 * @Description 仿照SpringSecurity中的 <code> AuthenticationManagerConfiguration </code>
 * Configuration for a Spring Security in-memory {@link AuthenticationManager}. Can be
 * disabled by providing a bean of type AuthenticationManager, or by autowiring an
 * {@link com.light.security.core.config.core.builder.AuthenticationManagerBuilder} into a method in one of your configuration
 * classes. The value provided by this configuration will become the "global"
 * authentication manager (from Spring Security), or the parent of the global instance.
 * Thus it acts as a fallback when no others are provided, is used by method security if
 * enabled, and as a parent authentication manager for "local" authentication managers in
 * individual filter chains.
 *
 * 翻译:
 * Spring Security内存{@link AuthenticationManager}的配置。 可以通过提供AuthenticationManager类型的Bean
 * 或通过将{@link com.light.security.core.config.core.builder.AuthenticationManagerBuilder}自动装配到其中一个配置类的方法中来禁用。
 * 该配置提供的值将成为“全局”身份验证管理器（来自Spring Security）或全局实例的父级。
 * 因此，它在没有提供其他任何身份的情况下充当后备角色，如果启用，则由方法安全性使用，并充当各个过滤器链中“本地”身份验证管理器的父身份验证管理器。
 *
 *
 * 小声BB:
 * 这, 是备胎
 * 不, 怎么能是备胎呢, 这是默认实现
 * @Author ZhouJian
 * @Date 2019-12-05
 */
@Configuration
@ConditionalOnBean({ObjectPostProcessor.class})
@ConditionalOnMissingBean({AuthenticationManager.class})
@Order(0)
public class AuthenticationManagerConfiguration {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    @Primary // 自动装配时当出现多个Bean候选者时，被注解为@Primary的Bean将作为首选者，否则将抛出异常
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // TODO: 2019-12-05 我看了SpringSecurity中关于这里的写法，其实意义在于给定一个默认实现，使用内存作为数据源(注册一个SubjectDetailService的配置器), 我就先不写这个了, 还是得写啊

}
