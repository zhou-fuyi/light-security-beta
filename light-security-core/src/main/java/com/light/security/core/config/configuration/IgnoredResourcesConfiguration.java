package com.light.security.core.config.configuration;

import com.light.security.core.config.core.IgnoredResourcesConfigurer;
import com.light.security.core.config.core.configurer.IgnoredResourcesConfigurerAdapter;
import com.light.security.core.properties.SecurityProperties;
import com.light.security.core.util.matcher.AntPathRequestMatcher;
import com.light.security.core.util.matcher.OrRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName IgnoredResourcesConfiguration
 * @Description 默认提供的一个公共资源过滤注册 配置器
 *
 * 注册一些默认情况下的公共资源, 也接受配置文件中的配置
 * @Author ZhouJian
 * @Date 2019-12-16
 */
@Configuration
public class IgnoredResourcesConfiguration implements IgnoredResourcesConfigurer, InitializingBean {

    private static final List<String> DEFAULT_IGNORED = Arrays.asList("/css/**", "/js/**", "/images/**", "/webjars/**", "/**/favicon.ico");

    private List<RequestMatcher> ignoredPathRequestMatchers = null;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void init() throws Exception {
        List<String> ignoredPaths = new ArrayList<>(DEFAULT_IGNORED);
        if (!CollectionUtils.isEmpty(securityProperties.getIgnored())){
            ignoredPaths.addAll(securityProperties.getIgnored());
        }
        ignoredPathRequestMatchers = new ArrayList<>(ignoredPaths.size());
        for (String ignoredPath : ignoredPaths){
            ignoredPathRequestMatchers.add(new AntPathRequestMatcher(ignoredPath));
        }
    }

    @Override
    public void ignoredRegistry(IgnoredResourcesConfigurerAdapter.IgnoredResourceRegistry ignoredResourceRegistry) throws Exception {
        /**
         * 这样就将需要忽略的路径匹配器归为一个, 有效的减少了过滤器链的数量
         */
        if (!CollectionUtils.isEmpty(ignoredPathRequestMatchers)){
            ignoredResourceRegistry.requestMatcher(new OrRequestMatcher(ignoredPathRequestMatchers));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(securityProperties, "SecurityProperties 不能为null");
    }
}
