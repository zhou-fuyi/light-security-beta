package com.light.security.core.authentication.dao.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcDaoProcessorManagerTest {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    JdbcDaoProcessorManager jdbcDaoProcessorManager;

    /**
     * 成功完成simple|advance模式的主体数据(包含主体数据与权限数据)获取测试
     */
    @Test
    public void loadSubjectBySubjectName() {
        String subjectName = "zhoujian";
        logger.info("subject is {}", jdbcDaoProcessorManager.loadSubjectBySubjectName(subjectName));
    }
}