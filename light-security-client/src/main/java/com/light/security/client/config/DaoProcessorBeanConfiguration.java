package com.light.security.client.config;

import com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessorManager;
import com.light.security.core.properties.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName DaoProcessorBeanConfiguration
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-10
 */
@Configuration
public class DaoProcessorBeanConfiguration {

    @Bean
    JdbcDaoProcessorManager jdbcDaoProcessorManager(){
        JdbcDaoProcessorManager jdbcDaoProcessorManager = new JdbcDaoProcessorManager();
        jdbcDaoProcessorManager.setAutoCreateTableOnStartup(true);
        return jdbcDaoProcessorManager;
    }

    @Bean
    public SecurityProperties securityProperties(){
        SecurityProperties securityProperties = new SecurityProperties();
        return securityProperties;
    }

}
