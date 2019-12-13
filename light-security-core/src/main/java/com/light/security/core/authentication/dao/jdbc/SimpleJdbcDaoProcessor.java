package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.model.*;
import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.properties.SecurityProperties;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @ClassName SimpleJdbcDaoProcessor
 * @Description 适用于简单模式 {@link com.light.security.core.properties.SecurityProperties.AuthTypeEnum#SIMPLE}下的账户查询
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public class SimpleJdbcDaoProcessor extends AbstractJdbcProcessor{

    private static final String SIMPLE_DDL_QUERY_FILENAME = "light-security-simple.ddl";

    public SimpleJdbcDaoProcessor(){
        setDdlQueryFilename(SIMPLE_DDL_QUERY_FILENAME);
    }

    @Override
    public Collection<GrantedRole> loadSubjectAuthorities(Integer subjectId) throws Exception{

        return getJdbcTemplate().query(JdbcQuery.getQuery(JdbcQuery.QueryKey.DEF_AUTHORITIES_BY_SUBJECT_ID_QUERY.name())
                , new Object[]{subjectId}
                , (ResultSetExtractor<Collection<GrantedRole>>) rs -> {
                    List<Role> roles = new ArrayList<>();
                    while (rs.next()){
                        roles.add(loadRole(rs));
                    }
                    postHandler(roles);
                    return comparatorAndTransformToGrantedRoleList(roles);
                });
    }

    @Override
    public boolean support(Enum authType) {
        return authType.name().equals(SecurityProperties.AuthTypeEnum.SIMPLE.name());
    }

    @Override
    public void autoInitTable(Enum authType) throws Exception {
//        createTable(SIMPLE_DDL_QUERY_FILENAME, authType);
        createTableWithSqlFile(authType);
    }

    @Override
    protected <T> void postHandler(List<T> list) {
        List<Role> roles = (List<Role>) list;
        Iterator<Role> roleIterator = roles.iterator();
        while (roleIterator.hasNext()){
            List<Authority> authorities = (List<Authority>) roleIterator.next().getAuthorities();
            Iterator<Authority> authorityIterator = authorities.iterator();
            while (authorityIterator.hasNext()){
                Authority authority = authorityIterator.next();
                if (!authority.isEnabled()){
                    authorityIterator.remove();
                }
            }
            if (authorities.size() == 0){
                roleIterator.remove();
            }
        }
    }

    private Role loadRole(ResultSet rs) throws SQLException {
        final String authType = rs.getString("type");
        if (StringUtils.isEmpty(authType))
            return null;
        Authority authority = null;

        // TODO: 2019-12-10 待改进
        /**
         * 下面出现部分重复代码, 后面需要改进
         */
        Integer parentId = rs.getInt("parentId") > 0 ? rs.getInt("parentId") : null;
        switch (authType){
            case "action":
                authority = new ActionAuthority.Builder(rs.getInt("authId"), rs.getString("code"), rs.getString("pattern"))
                        .method("method")
                        .parentId(parentId)
                        .name(rs.getString("name"))
                        .desc(rs.getString("desc"))
                        .enabled(rs.getBoolean("enabled"))
                        .opened(rs.getBoolean("opened"))
                        .type(rs.getString("type"))
                        .build();
                break;
            case "menu":
                authority = new MenuAuthority.Builder(rs.getInt("authId"), rs.getString("code"), rs.getString("link"))
                        .icon("icon")
                        .parentId(parentId)
                        .name(rs.getString("name"))
                        .desc(rs.getString("desc"))
                        .enabled(rs.getBoolean("enabled"))
                        .opened(rs.getBoolean("opened"))
                        .type(rs.getString("type"))
                        .build();
                break;
            case "element":
                authority = new ElementAuthority.Builder(rs.getInt("authId"), rs.getString("code"))
                        .parentId(parentId)
                        .name(rs.getString("name"))
                        .desc(rs.getString("desc"))
                        .enabled(rs.getBoolean("enabled"))
                        .opened(rs.getBoolean("opened"))
                        .type(rs.getString("type"))
                        .build();
                break;
            default:
                return null;
        }
        /**
         * 这里没有判断authority是否为空便直接作为参数构建role, 估计有bug
         */
        Role role = new DefaultRole.Builder(rs.getInt("roleId"), rs.getString("roleName"), new ArrayList<>(Arrays.asList(authority)))
                .roleCode(rs.getString("roleCode"))
                .roleDesc(rs.getString("roleDesc"))
                .build();
        return role;
    }

    @Override
    public void setEnabledGroups(boolean enabledGroups) {
        if (enabledGroups){
            throw new IllegalArgumentException("本处理器不支持组概念内容");
        }
        super.setEnabledGroups(false);
    }
}
