package com.light.security.configuration;

import com.light.security.properties.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SecurityPropertiesConfiguration
 * @Description 开启<code>ConfigurationProperties</code>支持
 * @Author ZhouJian
 * @Date 2019-11-18
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityPropertiesConfiguration {
}
