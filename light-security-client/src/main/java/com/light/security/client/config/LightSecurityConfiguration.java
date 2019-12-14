package com.light.security.client.config;

import com.light.security.core.access.AccessDecisionManager;
import com.light.security.core.access.vote.AuthorityAccessDecisionManager;
import com.light.security.core.access.vote.AuthorityVoter;
import com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;

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

    /**
     * 测试{@link com.light.security.core.config.core.AutowireBeanFactoryObjectPostProcessor}使用
     * @return
     */
//    @Bean
//    public AccessDecisionManager accessDecisionManager(){
//        return new AuthorityAccessDecisionManager(new ArrayList<>(Arrays.asList(new AuthorityVoter())));
//    }
}
