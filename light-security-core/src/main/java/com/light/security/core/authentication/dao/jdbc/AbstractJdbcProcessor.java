package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.subject.Subject;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.SubjectNameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName AbstractJdbcProcessor
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public abstract class AbstractJdbcProcessor extends JdbcDaoSupport implements JdbcDaoProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean enabledAuthorities = true;
    private boolean enabledGroups;

    @Override
    public SubjectDetail loadSubjectBySubjectName(String subjectName) throws SubjectNameNotFoundException {
        List<SubjectDetail> subjects = loadSubjectsBySubjectName(subjectName);
        if (CollectionUtils.isEmpty(subjects)){
            logger.debug("账户名 {} 不存在", subjectName);
            throw new SubjectNameNotFoundException(404, "SubjectName is " + subjectName + "找不到");
        }
        SubjectDetail subject = subjects.get(0);
        Collection<GrantedRole> roles = new ArrayList<>();
        if (enabledAuthorities){
            roles.addAll(loadSubjectAuthorities((Integer) subject.getKey()));
        }
        if (enabledGroups){
            roles.addAll(loadGroupAuthorities((Integer) subject.getKey()));
        }

        addAdditionalAuthorities(subject.getSubjectName(), roles);
        if (roles.size() == 0){
            logger.debug("账户: {} 没有获取到任何权限, 将会被作为账户不存在处理");
            throw new SubjectNameNotFoundException(401, "SubjectName is " + subjectName + "没有权限");
        }
        return createSubjectDetail(subjectName, subject, roles);
    }

    /**
     * 用于子类实现加载自己的权限, 比如配置文件等
     * 这里使用用户名作为区分
     * @param subjectName
     * @param roles
     */
    protected void addAdditionalAuthorities(String subjectName, Collection<GrantedRole> roles) {
    }

    @Override
    public SubjectDetail createSubjectDetail(String subjectName, SubjectDetail query, Collection<GrantedRole> roles) {
        /**
         * 你可以在这里干一些别的事, 我就先不作任何处理
         */
        return new Subject(query.getKey(), query.getSubjectName(), query.getPassword(), query.isEnabled(), true, true, true, roles);
    }

    @Override
    public List<SubjectDetail> loadSubjectsBySubjectName(String subjectName) throws SubjectNameNotFoundException {
        return getJdbcTemplate().query(JdbcQuery.getQuery(JdbcQuery.QueryKey.DEF_SUBJECTS_BY_SUBJECT_NAME_QUERY.name()),
                new String[]{subjectName}, new RowMapper<SubjectDetail>() {
                    @Override
                    public SubjectDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Integer id = rs.getInt(1);
                        String subjectName = rs.getString(2);
                        String password = rs.getString(3);
                        boolean enabled = rs.getBoolean(4);
                        return new Subject(id, subjectName, password, enabled, true, true, true, Collections.EMPTY_LIST);
                    }
                });
    }
}
