package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.SubjectNameNotFoundException;

/**
 * @InterfaceName DaoProcessorManager
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-10
 */
public interface DaoProcessorManager {

    SubjectDetail loadSubjectBySubjectName(String subjectName) throws SubjectNameNotFoundException;
}
