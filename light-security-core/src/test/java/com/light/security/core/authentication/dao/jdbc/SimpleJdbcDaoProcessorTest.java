package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.properties.SecurityProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleJdbcDaoProcessorTest {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private SimpleJdbcDaoProcessor simpleJdbcDaoProcessor;
    private SecurityProperties securityProperties;

    @Autowired
    public void setSimpleJdbcDaoProcessor(DataSource daoProcessor) {
        this.simpleJdbcDaoProcessor = new SimpleJdbcDaoProcessor();
        this.simpleJdbcDaoProcessor.setDataSource(daoProcessor);
    }

    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties){
        this.securityProperties = securityProperties;
    }

    @Before
    public void before(){
        if (securityProperties.getAuthType().name().equals(SecurityProperties.AuthTypeEnum.SIMPLE.name())){
            logger.info("支持模式为 --> {} <--的测试", securityProperties.getAuthType().name());
        }else {
            logger.warn("当前测试仅支持模式 --> {}", securityProperties.getAuthType().name());
            throw new IllegalArgumentException("当前测试仅支持模式 --> " + securityProperties.getAuthType().name());
        }
    }

    /**
     * 测试根据主体的id获取该主体关联的权限
     * simple模式已成功通过测试
     * @throws Exception
     */
    @Test
    public void loadSubjectAuthorities() throws Exception {
        System.out.println("datasource: " + simpleJdbcDaoProcessor.getDataSource());
        Collection<GrantedRole> grantedRoles = simpleJdbcDaoProcessor.loadSubjectAuthorities(1);
        logger.info("grantedRoles`s size is {}", grantedRoles.size());
        grantedRoles.forEach(item -> logger.info("item is {}", item));
    }

    /**
     * 测试根据主体名查询账户主体数据
     * simple模式已成功通过测试
     */
    @Test
    public void loadSubjectBySubjectName(){
        logger.info("subject: {}", simpleJdbcDaoProcessor.loadSubjectBySubjectName("zhoujian"));
    }
}