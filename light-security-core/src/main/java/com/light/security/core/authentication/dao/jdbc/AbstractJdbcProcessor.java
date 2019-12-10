package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.model.Role;
import com.light.security.core.access.role.DefaultGrantRole;
import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.subject.Subject;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.InternalAuthenticationServiceException;
import com.light.security.core.exception.InternalServiceException;
import com.light.security.core.exception.SubjectNameNotFoundException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @ClassName AbstractJdbcProcessor
 * @Description {@link JdbcDaoProcessor}的抽象实现, 在本实现中完成了{@link #loadSubjectBySubjectName(String)}方法, 获取账户的方法
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public abstract class AbstractJdbcProcessor extends JdbcDaoSupport implements JdbcDaoProcessor {

    private static final String DEFAULT_DDL_FILE_PREFIX = "support/ddl/";

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean enabledAuthorities = true;
    private boolean enabledGroups;
    private String ddlFilePrefix = DEFAULT_DDL_FILE_PREFIX;

    @Override
    public SubjectDetail loadSubjectBySubjectName(String subjectName) throws AuthenticationException {
        List<SubjectDetail> subjects = loadSubjectsBySubjectName(subjectName);
        if (CollectionUtils.isEmpty(subjects)){
            logger.debug("账户名 {} 不存在", subjectName);
            throw new SubjectNameNotFoundException(404, "SubjectName is " + subjectName + "找不到");
        }
        SubjectDetail subject = subjects.get(0);
        Collection<GrantedRole> roles = new ArrayList<>();
        if (enabledAuthorities){
            try {
                roles.addAll(loadSubjectAuthorities((Integer) subject.getKey()));
            }catch (Exception ex){
                logger.debug("数据库查询异常: {}", ex.getMessage());
                throw new InternalServiceException(500, "数据库查询异常");
            }
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

    /**
     * 该方法不同于{@link SubjectDetailService#loadSubjectBySubjectName(String)},
     * 该方法直接与数据库进行交互, 是<code> SubjectDetailService </code>接口中方法的数据库实现,
     * 在此方法中没有在数据返回的同时直接查询权限数据, 原因是避免多余的执行(如果当前的返回为空, 那么会造成NPE问题),
     * 虽然也可以先判断进行问题避免, 但是考虑到尽量让方法的职责边界明显, 所以将权限的加载放到了{@link SubjectDetailService#loadSubjectBySubjectName(String)}
     * 方法的实现中
     * @param subjectName
     * @return
     */
    protected List<SubjectDetail> loadSubjectsBySubjectName(String subjectName) throws SubjectNameNotFoundException {
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

    @Override
    public Collection<GrantedRole> loadGroupAuthorities(Integer subjectId) {
        if (logger.isDebugEnabled()){
            logger.debug("暂时不支持组概念");
        }
        throw new InternalAuthenticationServiceException(500, "暂时不支持组概念");
    }


    @Override
    public void autoInitTable(Enum authType) throws Exception {
        if (logger.isDebugEnabled()){
            logger.debug("do nothing");
        }
    }

    /**
     * 根据给定文件名读取文件进行表的创建, 子类可以重写该方法实现自己的逻辑
     *
     * 小声BB:
     * 这里其实就是创建表, 根据不同的模式创建不同模式下需要的表
     * 这里是将相关的ddl文件放置到资源目录下, 具体目录为: resource/support/ddl/
     *
     * 这里发现指定执行第一条语句
     * @param filename
     * @param currentAuthType 表示当前系统中支持的模式, 默认为{@link com.light.security.core.constant.AuthTypeEnum#SIMPLE}
     * @throws Exception
     */
    protected void createTable(String filename, Enum currentAuthType) throws Exception {
        if (StringUtils.isEmpty(filename) || !filename.substring(0, filename.indexOf(".")).endsWith(currentAuthType.name().toLowerCase(Locale.getDefault()))){
            throw new IllegalAccessException("参数异常 --> filename is empty or not matcher currentAuthType, filename: " + filename);
        }
        filename = ddlFilePrefix + filename;
        String ddlQuery = null;
        InputStream inputStream = null;
        ClassPathResource classPathResource = new ClassPathResource(filename);
        try {
            inputStream = classPathResource.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(byteBuffer)) != -1){
                bos.write(byteBuffer, 0, len);
            }
            bos.close();
            ddlQuery = bos.toString("utf-8");
        }catch (Exception e){
            if (logger.isDebugEnabled()){
                logger.debug("文件读取异常: {}", e.getMessage());
            }
            throw e;
        }
        if (StringUtils.isEmpty(ddlQuery)){
            throw new InternalServiceException(500, "文件读取异常, 获取文件数据为空");
        }
//        getJdbcTemplate().execute(ddlQuery);//只能执行第一条sql
        Statement statement = getJdbcTemplate().getDataSource().getConnection().createStatement();
        statement.executeUpdate(ddlQuery);
        statement.close();
    }

    /**
     * 用于执行sql文件
     * @param filename
     * @param currentAuthType
     * @throws Exception
     */
    protected void createTableWithSqlFile(String filename, Enum currentAuthType) throws Exception {
        if (StringUtils.isEmpty(filename) || !filename.substring(0, filename.indexOf(".")).endsWith(currentAuthType.name().toLowerCase(Locale.getDefault()))){
            throw new IllegalAccessException("参数异常 --> filename is empty or not matcher currentAuthType, filename: " + filename);
        }
        filename = ddlFilePrefix + filename;
        ClassPathResource classPathResource = new ClassPathResource(filename);
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(classPathResource);
        logger.info(ToStringBuilder.reflectionToString(databasePopulator, ToStringStyle.MULTI_LINE_STYLE));
        logger.info("开始执行SQL文件");
        logger.info("datasource: {}", getDataSource());
        databasePopulator.execute(getDataSource());
    }

    /**
     * 用于对角色排序和转换
     * @param roles
     * @return
     */
    protected Collection<GrantedRole> comparatorAndTransformToGrantedRoleList(List<Role> roles){
        roles.sort(Comparator.comparingInt(Role::getKey));
        Map<Integer, Role> roleBuffer = new HashMap<>();
        Role previousRole = null;
        for (Role role : roles){
            if (previousRole != null && previousRole.getKey() == role.getKey()){
                /**
                 * 这个if条件好像不会触发, 除非循环中的role就是null
                 */
                if (roleBuffer.get(previousRole.getKey()) == null){
                    roleBuffer.put(previousRole.getKey(), previousRole);
                }

                roleBuffer.get(role.getKey()).addAuthorities(role.getAuthorities());
                continue;
            }
            previousRole = role;
            roleBuffer.put(role.getKey(), role);
        }
        return transformToGrantedRole(new ArrayList<>(roleBuffer.values()));
    }

    /**
     * 将{@link Role}转换为{@link GrantedRole}, 当然, 这里是集合转换
     * @param handledRoles
     * @return
     */
    protected Collection<GrantedRole> transformToGrantedRole(List<Role> handledRoles){
        List<GrantedRole> grantedRoles = new ArrayList<>(handledRoles.size());
        handledRoles.stream().forEach(role -> grantedRoles.add(new DefaultGrantRole(role)));
        return grantedRoles;
    }

    @Override
    protected void initDao() throws Exception {
        Assert.notNull(getDataSource(), "数据源不能为空");
    }
}
