package com.light.security.core.config.core.autowired;

import com.light.security.core.config.core.SecurityConfigurer;
import com.light.security.core.config.core.WebSecurityConfigurer;
import com.light.security.core.config.core.builder.ChainProxyBuilder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AutowiredWebSecurityConfigurersIgnoreParents
 * @Description 用于收集 {@link WebSecurityConfigurer}的子类实例
 * @Author ZhouJian
 * @Date 2019-12-04
 */
public class AutowiredWebSecurityConfigurersIgnoreParents {

    private final ConfigurableListableBeanFactory beanFactory;

    public AutowiredWebSecurityConfigurersIgnoreParents(ConfigurableListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "beanFactory cannot be null");
        this.beanFactory = beanFactory;
    }

    /**
     * 收集{@link WebSecurityConfigurer}的子类实例
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<SecurityConfigurer<Filter, ChainProxyBuilder>> getWebSecurityConfigurers() {
        List<SecurityConfigurer<Filter, ChainProxyBuilder>> webSecurityConfigurers = new ArrayList<>();
        Map<String, WebSecurityConfigurer> beansOfType = beanFactory.getBeansOfType(WebSecurityConfigurer.class);
        for (Map.Entry<String, WebSecurityConfigurer> entry : beansOfType.entrySet()) {
            webSecurityConfigurers.add(entry.getValue());
        }
        return webSecurityConfigurers;
    }
}
