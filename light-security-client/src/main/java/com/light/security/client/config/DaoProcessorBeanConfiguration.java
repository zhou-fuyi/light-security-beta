package com.light.security.client.config;

import com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName DaoProcessorBeanConfiguration
 * @Description 用户注册 {@link com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessor}相关的Bean, 可以覆盖默认配置
 * @Author ZhouJian
 * @Date 2019-12-10
 */
@Configuration
public class DaoProcessorBeanConfiguration {

    @Bean
    JdbcDaoProcessorManager jdbcDaoProcessorManager(){
        JdbcDaoProcessorManager jdbcDaoProcessorManager = new JdbcDaoProcessorManager();
//        jdbcDaoProcessorManager.setAutoCreateTableOnStartup(true);
        return jdbcDaoProcessorManager;
    }

}
