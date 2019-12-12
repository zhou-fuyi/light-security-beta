package com.light.security.core;

import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessorManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName LightSecurityCoreApplication
 * @Description 为了单独测试core模块
 * @Author ZhouJian
 * @Date 2019-12-12
 */
@SpringBootApplication
public class LightSecurityCoreApplication {

    public static void main(String[] args){
        SpringApplication.run(LightSecurityCoreApplication.class, args);
    }

//    ============================================测试需要=================================================

    @Bean
    JdbcDaoProcessorManager jdbcDaoProcessorManager(){
        JdbcDaoProcessorManager jdbcDaoProcessorManager = new JdbcDaoProcessorManager();
//        jdbcDaoProcessorManager.setAutoCreateTableOnStartup(true);
        return jdbcDaoProcessorManager;
    }

    @Bean
    SubjectDetailService defaultSubjectService(JdbcDaoProcessorManager jdbcDaoProcessorManager){
        return subjectName -> jdbcDaoProcessorManager.loadSubjectBySubjectName(subjectName);
    }
}
