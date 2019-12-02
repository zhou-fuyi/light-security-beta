package com.light.security.core.authentication;

import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.SubjectNameNotFoundException;

/**
 * @InterfaceName SubjectDetailService
 * @Description 用户获取 {@link SubjectDetail}, 可以自定义实现方式, 定义数据来源
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public interface SubjectDetailService {

    SubjectDetail loadSubjectBySubjectName(String subjectName) throws SubjectNameNotFoundException;
}
