package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.constant.AuthTypeEnum;

import java.util.Collection;

/**
 * @ClassName GroupJdbcDaoProcessor
 * @Description 适用于组概念模式 {@link AuthTypeEnum#GROUP}下的账户查询
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public class GroupJdbcDaoProcessor extends AbstractJdbcProcessor {
    @Override
    public Collection<GrantedRole> loadSubjectAuthorities(Integer subjectId) {
        return null;
    }


    @Override
    public boolean support(Enum authType) {
        return authType.name().equals(AuthTypeEnum.GROUP.name());
    }
}
