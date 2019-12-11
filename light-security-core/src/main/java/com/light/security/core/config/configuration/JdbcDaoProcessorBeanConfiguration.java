package com.light.security.core.config.configuration;

import com.light.security.core.access.model.ActionAuthority;
import com.light.security.core.authentication.dao.jdbc.AdvanceJdbcDaoProcessor;
import com.light.security.core.authentication.dao.jdbc.GroupJdbcDaoProcessor;
import com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessorManager;
import com.light.security.core.authentication.dao.jdbc.SimpleJdbcDaoProcessor;
import com.light.security.core.authentication.dao.jdbc.auth.ActionAuthorityDaoProcessor;
import com.light.security.core.authentication.dao.jdbc.auth.ElementAuthorityDaoProcessor;
import com.light.security.core.authentication.dao.jdbc.auth.MenuAuthorityDaoProcessor;
import com.light.security.core.properties.SecurityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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


    /**
     * 注册{@link com.light.security.core.authentication.dao.jdbc.DaoProcessorManager}
     * 用于根据当前系统配置, 调用适配的 {@link com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessor}进行操作
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(JdbcDaoProcessorManager.class)
    public JdbcDaoProcessorManager jdbcDaoProcessorManager(){
        return new JdbcDaoProcessorManager();
    }

    /**
     * 注册简易模式的{@link com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessor}
     * @param dataSource
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(SimpleJdbcDaoProcessor.class)
    public SimpleJdbcDaoProcessor simpleJdbcDaoProcessor(DataSource dataSource){
        SimpleJdbcDaoProcessor simpleJdbcDaoProcessor = new SimpleJdbcDaoProcessor();
        simpleJdbcDaoProcessor.setDataSource(dataSource);
        return simpleJdbcDaoProcessor;
    }

    /**
     * 注册进阶模式的{@link com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessor}
     * @param dataSource
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = {"light.security.authType"}, havingValue = "advance")
    @ConditionalOnMissingBean(AdvanceJdbcDaoProcessor.class)
    public AdvanceJdbcDaoProcessor advanceJdbcDaoProcessor(DataSource dataSource){
        AdvanceJdbcDaoProcessor advanceJdbcDaoProcessor = new AdvanceJdbcDaoProcessor();
        advanceJdbcDaoProcessor.setDataSource(dataSource);
        return advanceJdbcDaoProcessor;
    }

    @Bean
    @ConditionalOnProperty(name = "light.security.authType", havingValue = "group")
    @ConditionalOnMissingBean(GroupJdbcDaoProcessor.class)
    public GroupJdbcDaoProcessor groupJdbcDaoProcessor(DataSource dataSource){
        GroupJdbcDaoProcessor groupJdbcDaoProcessor = new GroupJdbcDaoProcessor();
        groupJdbcDaoProcessor.setDataSource(dataSource);
        return groupJdbcDaoProcessor;
    }


//    下面注册的类用于存在权限分类且分表存储的情况

    @Bean
    @ConditionalOnMissingBean(ActionAuthorityDaoProcessor.class)
    public ActionAuthorityDaoProcessor actionAuthorityDaoProcessor(DataSource dataSource){
        ActionAuthorityDaoProcessor actionAuthorityDaoProcessor = new ActionAuthorityDaoProcessor(dataSource);
        return actionAuthorityDaoProcessor;
    }

    @Bean
    @ConditionalOnMissingBean(MenuAuthorityDaoProcessor.class)
    public MenuAuthorityDaoProcessor menuAuthorityDaoProcessor(DataSource dataSource){
        MenuAuthorityDaoProcessor menuAuthorityDaoProcessor = new MenuAuthorityDaoProcessor(dataSource);
        return menuAuthorityDaoProcessor;
    }

    @Bean
    @ConditionalOnMissingBean(ElementAuthorityDaoProcessor.class)
    public ElementAuthorityDaoProcessor elementAuthorityDaoProcessor(DataSource dataSource){
        ElementAuthorityDaoProcessor elementAuthorityDaoProcessor = new ElementAuthorityDaoProcessor(dataSource);
        return elementAuthorityDaoProcessor;
    }

}
