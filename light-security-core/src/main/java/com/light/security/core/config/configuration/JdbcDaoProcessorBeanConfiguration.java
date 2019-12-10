package com.light.security.core.config.configuration;

import com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessorManager;
import com.light.security.core.authentication.dao.jdbc.SimpleJdbcDaoProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @ClassName JdbcDaoProcessorBeanConfiguration
 * @Description {@link com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessor}相关Bean的配置
 * @Author ZhouJian
 * @Date 2019-12-10
 */
@Configuration
public class JdbcDaoProcessorBeanConfiguration {


    @Bean
    @ConditionalOnMissingBean(JdbcDaoProcessorManager.class)
    public JdbcDaoProcessorManager jdbcDaoProcessorManager(){
        return new JdbcDaoProcessorManager();
    }

    @Bean
    @ConditionalOnMissingBean(SimpleJdbcDaoProcessor.class)
    public SimpleJdbcDaoProcessor simpleJdbcDaoProcessor(DataSource dataSource){
        SimpleJdbcDaoProcessor simpleJdbcDaoProcessor = new SimpleJdbcDaoProcessor();
        simpleJdbcDaoProcessor.setDataSource(dataSource);
        return simpleJdbcDaoProcessor;
    }

}
