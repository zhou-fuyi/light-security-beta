package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.SubjectNameNotFoundException;
import org.springframework.beans.factory.InitializingBean;

/**
 * @InterfaceName DaoProcessorManager
 * @Description 模仿 {@link com.light.security.core.authentication.AuthenticationManager}的行为模式,
 * 根据系统当前 {@link com.light.security.core.properties.SecurityProperties.AuthTypeEnum}的模式
 * 选择适配的{@link JdbcDaoProcessor}进行处理
 * @Author ZhouJian
 * @Date 2019-12-10
 */
public interface DaoProcessorManager extends InitializingBean {

    /**
     * 尝试从数据库从根据账户主体名称获取账户, 如果获取失败则抛出{@link SubjectNameNotFoundException}异常
     * @param subjectName
     * @return
     * @throws SubjectNameNotFoundException
     */
    SubjectDetail loadSubjectBySubjectName(String subjectName) throws SubjectNameNotFoundException;
}
