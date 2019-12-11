package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.model.*;
import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.dao.jdbc.auth.AuthorityDaoProcessor;
import com.light.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @ClassName AdvanceJdbcDaoProcessor
 * @Description 适用于进阶模式 {@link com.light.security.core.properties.SecurityProperties.AuthTypeEnum#ADVANCE}下的账户查询
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public class AdvanceJdbcDaoProcessor extends AbstractAdditionJdbcProcessor {

    private static final String ADVANCE_DDL_QUERY_FILENAME = "light-security-advance.ddl";

    public AdvanceJdbcDaoProcessor(){
        setDdlQueryFilename(ADVANCE_DDL_QUERY_FILENAME);
    }

    /**
     * 该方法自动化程度较低, 但是
     *
     * 但是什么但是, 没有但是   --> go
     * @param subjectId
     * @return
     */
    @Override
    public Collection<GrantedRole> loadSubjectAuthorities(Integer subjectId) {
        /**
         * 1、获取根据subjectId获取到authority集合
         *
         * 2、根据authority集合以及每一则authority权限类型查询对应的数据, 拼装为一个新的authority集合
         *
         * 3、将所有数据以role进行组织, 进行排序并去除role中可能出现的残余笛卡尔积的影响
         *
         * 4、返回数据
         */

        List<Role> roleMapperList = getJdbcTemplate().query(JdbcQuery.getQuery(JdbcQuery.QueryKey.DEF_AUTHORITIES_BY_SUBJECT_ID_QUERY.name())
                , new Object[]{subjectId}
                , new ResultSetExtractor<List<Role>>() {
                    @Override
                    public List<Role> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Role> roles = new ArrayList<>();
                        while (rs.next()){
                            Authority authority = new SimpleAssistAuthority(rs.getInt("authId"), rs.getString("type"));
                            Role role = new DefaultRole.Builder(rs.getInt("roleId"), rs.getString("roleName"), Arrays.asList(authority))
                                    .roleCode(rs.getString("roleCode"))
                                    .roleDesc(rs.getString("roleDesc"))
                                    .build();
                            roles.add(role);
                        }
                        return roles;
                    }
                });

        return comparatorAndTransformToGrantedRoleList(roleMapperList);
    }

    @Override
    public boolean support(Enum authType) {
        return authType.name().equals(SecurityProperties.AuthTypeEnum.ADVANCE.name());
    }

    @Override
    public void autoInitTable(Enum authType) throws Exception {
        createTableWithSqlFile(authType);
    }

    @Override
    public void setEnabledGroups(boolean enabledGroups) {
        if (enabledGroups){
            throw new IllegalArgumentException("本处理器不支持组概念内容");
        }
        super.setEnabledGroups(false);
    }
}
