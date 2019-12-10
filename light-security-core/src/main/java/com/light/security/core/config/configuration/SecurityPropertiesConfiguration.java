package com.light.security.core.config.configuration;

import com.light.security.core.properties.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SecurityPropertiesConfiguration
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-10
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)// 开启<code> ConfigurationProperties </code>支持
public class SecurityPropertiesConfiguration {
}
