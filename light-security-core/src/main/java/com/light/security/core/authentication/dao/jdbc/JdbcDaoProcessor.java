package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.AuthenticationException;

import java.util.Collection;

/**
 * @InterfaceName JdbcDaoProcessor
 * @Description 用于处理不同模式的数据交互
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public interface JdbcDaoProcessor {

    SubjectDetail loadSubjectBySubjectName(String subjectName) throws AuthenticationException;

    /**
     * 根据给定数据创建一个返回的{@link SubjectDetail}
     *
     * @param subjectName
     * @param query
     * @param roles
     * @return
     */
    SubjectDetail createSubjectDetail(String subjectName, SubjectDetail query, Collection<GrantedRole> roles);


    /**
     * 根据{@link SubjectDetail}的ID加载权限数据
     * 使用角色{@link GrantedRole}进行权限数据的组织
     * @param subjectId
     * @return
     */
    Collection<GrantedRole> loadSubjectAuthorities(Integer subjectId) throws Exception;


    /**
     * 根据{@link SubjectDetail}的ID加载组概念关联的权限数据
     * 使用角色{@link GrantedRole}进行权限数据的组织
     * @param subjectId
     * @return
     */
    Collection<GrantedRole> loadGroupAuthorities(Integer subjectId);

    /**
     * 可以实现该方法, 用于创建表
     */
    void autoInitTable(Enum authType) throws Exception ;

    boolean support(Enum authType);
}
