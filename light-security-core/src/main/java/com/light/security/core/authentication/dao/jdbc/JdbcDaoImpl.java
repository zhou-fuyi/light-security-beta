package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.SubjectNameNotFoundException;
import com.light.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * @ClassName JdbcDaoImpl
 * @Description 仿照SpringSecurity中JdbcDaoImpl完成
 *
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public class JdbcDaoImpl extends JdbcDaoSupport implements SubjectDetailService {

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public SubjectDetail loadSubjectBySubjectName(String subjectName) throws SubjectNameNotFoundException {
        return null;
    }

    @Override
    protected void initDao() throws Exception {
        // 可以进行数据表的初始化
    }
}
