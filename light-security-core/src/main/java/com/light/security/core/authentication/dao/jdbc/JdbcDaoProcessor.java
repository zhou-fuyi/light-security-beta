package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.subject.SubjectDetail;

import java.util.Collection;
import java.util.List;

/**
 * @InterfaceName JdbcDaoProcessor
 * @Description 用于处理不同模式的数据交互
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public interface JdbcDaoProcessor extends SubjectDetailService{

    SubjectDetail createSubjectDetail(String subjectName, SubjectDetail query, Collection<GrantedRole> roles);

    List<SubjectDetail> loadSubjectsBySubjectName(String subjectName);

    /**
     * 根据Subject
     * @param subjectId
     * @return
     */
    Collection<GrantedRole> loadSubjectAuthorities(Integer subjectId);


    Collection<GrantedRole> loadGroupAuthorities(Integer subjectId);
}
