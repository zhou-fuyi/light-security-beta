package com.light.security.core.config.autowired;

import com.light.security.core.config.annotation.EnabledLightSecurity;
import com.light.security.core.config.configuration.WebSecurityConfiguration;
import com.light.security.core.config.core.WebSecurityConfigurer;
import com.light.security.core.config.core.builder.ChainProxyBuilder;
import com.light.security.core.properties.SecurityProperties;
import com.light.security.core.util.matcher.AntPathRequestMatcher;
import com.light.security.core.util.matcher.OrRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName SpringBootWebSecurityConfiguration
 * @Description 模仿SpringSecurity中 <code> SpringBootWebSecurityConfiguration </code>
 * 注册需要忽略的路径
 * @Author ZhouJian
 * @Date 2019-12-05
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({EnabledLightSecurity.class})
@ConditionalOnMissingBean(WebSecurityConfiguration.class)// 不是很理解这句话的意思, 可能和加载顺序有很大关系吧
@EnabledLightSecurity
public class SpringBootWebSecurityConfiguration {

    private static final List<String> DEFAULT_IGNORED = Arrays.asList("/css/**", "/js/**", "/images/**", "/webjars/**", "/**/favicon.ico");

    /**
     * 用于注册需要忽略的访问路径
     * @param securityProperties
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean(value = WebSecurityConfigurer.class, name = "ignoredPathsWebSecurityConfigurerAdapter")
    public WebSecurityConfigurer<ChainProxyBuilder> ignoredPathsWebSecurityConfigurerAdapter(SecurityProperties securityProperties){
        return new WebSecurityConfigurer<ChainProxyBuilder>() {
            @Override
            public void init(ChainProxyBuilder builder) throws Exception {

            }

            @Override
            public void configure(ChainProxyBuilder builder) throws Exception {
                List<String> ignoredPaths = new ArrayList<>(DEFAULT_IGNORED);
                if (!CollectionUtils.isEmpty(securityProperties.getIgnored())){
                    ignoredPaths.addAll(securityProperties.getIgnored());
                }
                List<RequestMatcher> ignoredMatchers = new ArrayList<>(ignoredPaths.size());
                for (String ignoredPath : ignoredPaths){
                    ignoredMatchers.add(new AntPathRequestMatcher(ignoredPath));
                }
                /**
                 * 这样就将需要忽略的路径匹配器归为一个, 有效的减少了过滤器链的数量
                 */
                if (!ignoredMatchers.isEmpty()){
                    builder.ignoredRequestRegistry().requestMatchers(new OrRequestMatcher(ignoredMatchers));
                }
            }
        };
    }

}
