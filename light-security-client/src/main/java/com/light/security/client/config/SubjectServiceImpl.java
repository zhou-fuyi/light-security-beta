package com.light.security.client.config;

import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessorManager;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.SubjectNameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;



/**
 * @ClassName SubjectServiceImpl
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-05
 */
@Configuration
public class SubjectServiceImpl implements SubjectDetailService {

    @Autowired
    private JdbcDaoProcessorManager jdbcDaoProcessorManager;

    @Override
    public SubjectDetail loadSubjectBySubjectName(String subjectName) throws SubjectNameNotFoundException {

        return jdbcDaoProcessorManager.loadSubjectBySubjectName(subjectName);
    }
}
