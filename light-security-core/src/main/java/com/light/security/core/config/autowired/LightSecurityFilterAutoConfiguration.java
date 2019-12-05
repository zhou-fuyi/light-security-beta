package com.light.security.core.config.autowired;

import com.light.security.core.config.AbstractSecurityWebApplicationInitializer;
import com.light.security.core.properties.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName LightSecurityFilterAutoConfiguration
 * @Description 用于构建并启动 {@link org.springframework.web.filter.DelegatingFilterProxy}, 这是内置容器的启动流程, 属于应对外置容器的兼容方法
 *
 * 模仿SpringSecurity中 --> <code> SecurityFilterAutoConfiguration </code>
 *
 * @Author ZhouJian
 * @Date 2019-12-05
 */

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({AbstractSecurityWebApplicationInitializer.class})
@AutoConfigureAfter({LightSecurityAutoConfiguration.class})
public class LightSecurityFilterAutoConfiguration {

    /**
     * {@link org.springframework.web.filter.DelegatingFilterProxy}的默认名称
     */
    private static final String DEFAULT_PROXY_FILTER_NAME = AbstractSecurityWebApplicationInitializer.DEFAULT_LIGHT_SECURITY_FILTER_NAME;

    /**
     * 仅当在<code> name = DEFAULT_PROXY_FILTER_NAME </code>的Bean已经初始化后才会执行该类的初始化
     * @param securityProperties
     * @return
     */
    @Bean
    @ConditionalOnBean(name = DEFAULT_PROXY_FILTER_NAME)
    public DelegatingFilterProxyRegistrationBean lightSecurityDelegatingFilterProxyRegistrationBean(SecurityProperties securityProperties){
        DelegatingFilterProxyRegistrationBean registrationBean = new DelegatingFilterProxyRegistrationBean(AbstractSecurityWebApplicationInitializer.DEFAULT_LIGHT_SECURITY_FILTER_NAME);
        registrationBean.setOrder(securityProperties.getFilter().getProxy().getProxyOrder());
        registrationBean.setDispatcherTypes(getDispatcherTypes(securityProperties));
        return registrationBean;
    }

    /**
     * 获取DispatcherTypes
     * @param securityProperties
     * @return
     */
    private EnumSet<DispatcherType> getDispatcherTypes(SecurityProperties securityProperties) {
        if (securityProperties.getFilter().getProxy().getFilterDispatcherTypes() == null) {
            // 默认范围
            /**
             * Filter是有作用范围的, 平时使用的Filter都是作用于Request(默认值)
             */
            return EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC);
        }
        Set<DispatcherType> dispatcherTypes = new HashSet<>();
        for (String dispatcherType : securityProperties.getFilter().getProxy().getFilterDispatcherTypes()) {
            dispatcherTypes.add(DispatcherType.valueOf(dispatcherType));
        }
        return EnumSet.copyOf(dispatcherTypes);
    }

}
