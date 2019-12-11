package com.light.security.core.authentication.dao.jdbc.auth;

import com.light.security.core.access.model.ActionAuthority;
import com.light.security.core.access.model.AssistAuthority;
import com.light.security.core.access.model.Authority;
import com.light.security.core.authentication.dao.jdbc.JdbcQuery;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ActionAuthorityDaoProcessor
 * @Description 用于处理 <code> action </code>类型的的权限
 * @Author ZhouJian
 * @Date 2019-12-11
 */
public class ActionAuthorityDaoProcessor extends AbstractAuthorityDaoProcessor  {

    private static final String SUPPORT_AUTHORITY_TYPE = "action";

    public ActionAuthorityDaoProcessor(DataSource dataSource) {
        super(dataSource, SUPPORT_AUTHORITY_TYPE);
    }

    @Override
    public List<Authority> loadAuthorities(List<AssistAuthority> assistAuthorities) {
        List<Integer> assistAuthorityIds = assistAuthorities.stream().map(AssistAuthority::getAuthorityId).collect(Collectors.toList());
        return getJdbcTemplate().query(JdbcQuery.getQuery(JdbcQuery.QueryKey.DEF_ACTION_AUTHORITIES_QUERY_BATCH.name())
                , new Object[]{ assistAuthorityIds }, new ResultSetExtractor<List<Authority>>() {
                    @Override
                    public List<Authority> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Authority> authorities = new ArrayList<>(assistAuthorityIds.size());
                        while (rs.next()){
                            Authority authority = new ActionAuthority.Builder(rs.getInt("id"), rs.getString("code"), rs.getString("pattern"))
                                    .method(rs.getString("method"))
                                    .name(rs.getString("name"))
                                    .desc(rs.getString("desc"))
                                    .enabled(rs.getBoolean("enabled"))
                                    .opened(rs.getBoolean("opened"))
                                    .type(rs.getString("type"))
                                    .build();
                        }
                        return authorities;
                    }
                });
    }
}
