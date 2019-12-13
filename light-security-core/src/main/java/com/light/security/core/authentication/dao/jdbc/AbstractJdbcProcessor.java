package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.model.ActionAuthority;
import com.light.security.core.access.model.Authority;
import com.light.security.core.access.model.Role;
import com.light.security.core.access.role.DefaultGrantRole;
import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.dao.jdbc.auth.AuthorityTypeEnum;
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
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @ClassName AbstractJdbcProcessor
 * @Description {@link JdbcDaoProcessor}的抽象实现, 在本实现中完成了{@link #loadSubjectBySubjectName(String)}方法, 获取账户的方法
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public abstract class AbstractJdbcProcessor extends JdbcDaoSupport implements JdbcDaoProcessor {

    private static final String DEFAULT_DDL_FILE_PREFIX = "support/ddl/";
    private static final String SEPARATOR = ";";

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean enabledAuthorities = true;
    private boolean enabledGroups;
    private String ddlFilePrefix = DEFAULT_DDL_FILE_PREFIX;
    private String separator = SEPARATOR;
    private String ddlQueryFilename;

    public boolean isEnabledAuthorities() {
        return enabledAuthorities;
    }

    public void setEnabledAuthorities(boolean enabledAuthorities) {
        this.enabledAuthorities = enabledAuthorities;
    }

    public boolean isEnabledGroups() {
        return enabledGroups;
    }

    public void setEnabledGroups(boolean enabledGroups) {
        this.enabledGroups = enabledGroups;
    }

    public String getDdlFilePrefix() {
        return ddlFilePrefix;
    }

    public void setDdlFilePrefix(String ddlFilePrefix) {
        this.ddlFilePrefix = ddlFilePrefix;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getDdlQueryFilename() {
        return ddlQueryFilename;
    }

    public void setDdlQueryFilename(String ddlQueryFilename) {
        this.ddlQueryFilename = ddlQueryFilename;
    }

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

    /**
     * 默认查询API权限, 子类可以重写该方法实现自己的逻辑
     * @return
     */
    @Override
    public Collection<Authority> loadSecurityMetadataOnStartup() throws Exception {
        return getJdbcTemplate().query(JdbcQuery.getQuery(JdbcQuery.QueryKey.DEF_SECURITY_METADATA_ON_STARTUP_QUERY.name())
                , new Object[]{AuthorityTypeEnum.ACTION.name()}, new ResultSetExtractor<Collection<Authority>>() {
                    @Override
                    public Collection<Authority> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Authority> securityMetadata = new ArrayList<>();
                        while (rs.next()){
                            //自增Id不能从0开始
                            Integer parentId = rs.getInt("parentId") > 0 ? rs.getInt("parentId") : null;
                            Authority authority = new ActionAuthority.Builder(rs.getInt("authId"), rs.getString("code"), rs.getString("pattern"))
                                    .method(rs.getString("method"))
                                    .type(rs.getString("type"))
                                    .parentId(parentId)
                                    .name(rs.getString("name"))
                                    .desc(rs.getString("desc"))
                                    .enabled(rs.getBoolean("enabled"))
                                    .opened(rs.getBoolean("opened"))
                                    .build();
                            securityMetadata.add(authority);
                        }
                        return securityMetadata;
                    }
                });
    }

    @Override
    public void autoInitTable(Enum authType) throws Exception {
        if (logger.isDebugEnabled()){
            logger.debug("do nothing");
        }
    }

    /**
     * 创建表, 但是只支持单条sql, 因为不管你传入多少条, 我只执行第一条
     * (只执行第一条是{@link org.springframework.jdbc.core.JdbcTemplate}的默认行为)
     * 我将使用 ";"作为分隔点进行分隔, 限制只能传入一条SQL
     * @param singleSql
     * @throws Exception
     */
    protected void createTableWithSingleQuery(String singleSql) throws Exception {
        singleSql = singleSql.trim();
        final boolean debug = logger.isDebugEnabled();
        if (StringUtils.isEmpty(singleSql)){
            if (debug){
                logger.debug("传入SQL语句为空");
            }
            throw new IllegalArgumentException("传入参数不能为空 --> sql is empty");
        }
        String[] splitBySeparator = singleSql.split(this.separator);
        if (splitBySeparator.length > 1){
            if (debug){
                logger.debug("传入SQL语句格式错误, 请检查是否使用\";\"进行语句结束, \";\"是默认分割器, 当前分割器为: {}", this.separator);
            }
            throw new IllegalArgumentException("传入参数错误, 不能传入多条SQL语句(默认使用\";\"作为分割器)");
        }
        getJdbcTemplate().execute(singleSql);
    }

    /**
     * 用于执行sql文件
     *
     * 小声BB:
     * 这里其实就是创建表, 根据不同的模式创建不同模式下需要的表
     * 这里是将相关的ddl文件放置到资源目录下, 具体目录为: resource/support/ddl/
     *
     * @param filename
     * @param currentAuthType
     * @throws Exception
     */
    protected void createTableWithSqlFile(String filename, Enum currentAuthType) throws Exception {
        if (StringUtils.isEmpty(filename) || !filename.substring(0, filename.indexOf(".")).endsWith(currentAuthType.name().toLowerCase(Locale.getDefault()))){
            throw new IllegalAccessException("参数异常 --> filename is empty or not matcher currentAuthType, filename: " + filename);
        }
        filename = ddlFilePrefix + filename;
        /**
         * 引发问题原因: 想要一次性执行多条Create语句, 无奈发现{@link org.springframework.jdbc.core.JdbcTemplate#execute(String)}
         * 方法一次只能执行一条语句(即使传入多条, 也只会执行第一条)
         *
         * 下方代码来自网络, 可参见: https://stackoverrun.com/cn/q/8456498
         *
         * 其实自己也能达到这个目的, 关键在于解析传入的多条语句(如来自SQL文件), 然后循环执行.
         * 其实下面的代码也是这样实现的, 可以看其源码(定个坐标 {@link org.springframework.jdbc.datasource.init.ScriptUtils#executeSqlScript(Connection, EncodedResource, boolean, boolean, String, String, String, String)})
         *
         * 警告警告: 这里一定要注意一个问题: SQL文件支持两种注释方式(1: "--", 2: "#"), 但是在这里最好是不要使用
         * 第二种进行注释, 不然很有可能会出问题, 这个原因在于对sql文件的解析(分割位置, 注释过滤等), 这也是我遇上了
         * 这个问题, 断点调试的时候发现, 不过并没有深入研究源码. 具体你可以看我的SQL文件中注释使用
         * resources/support/ddl/目录下用于存放DDL_SQL文件
         *
         */
        ClassPathResource classPathResource = new ClassPathResource(filename);
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(classPathResource);
        logger.info(ToStringBuilder.reflectionToString(databasePopulator, ToStringStyle.MULTI_LINE_STYLE));
        logger.info("开始执行SQL文件");
        logger.info("datasource: {}", getDataSource());
        databasePopulator.execute(getDataSource());
    }

    /**
     * 用于执行sql文件
     *
     * 小声BB:
     * 这里其实就是创建表, 根据不同的模式创建不同模式下需要的表
     * 这里是将相关的ddl文件放置到资源目录下, 具体目录为: resource/support/ddl/
     *
     * @param currentAuthType
     * @throws Exception
     */
    protected void createTableWithSqlFile(Enum currentAuthType) throws Exception {
        createTableWithSqlFile(this.ddlQueryFilename, currentAuthType);
    }

    /**
     * 后处理器, 目前主要是在{@link SimpleJdbcDaoProcessor}中进行了重写, 用于处理权限中 enabled = false 的情况
     * @param list
     * @param <T>
     * @return
     */
    protected <T> void postHandler(List<T> list){

    }

    /**
     * 用于对角色排序和转换
     * @param roles
     * @return
     */
    protected Collection<GrantedRole> comparatorAndTransformToGrantedRoleList(Collection<Role> roles){
        List<Role> comparatorRoles = comparatorAndRepeatMergeRoleList(roles);
        return transformToGrantedRole(comparatorRoles);
    }

    /**
     * 对传入角色进行排序以及重复角色合并
     * @param roles
     * @return
     */
    protected List<Role> comparatorAndRepeatMergeRoleList(Collection<Role> roles){
        Map<Integer, Role> roleBuffer = new HashMap<>();
        List<Role> handleRoles = new ArrayList<>(roles);
        handleRoles.sort(Comparator.comparingInt(Role::getKey));
        Role previousRole = null;
        for (Role role : handleRoles){
            if (previousRole != null && previousRole.getKey() == role.getKey()){
                /**
                 * 这个if条件好像不会触发, 除非循环中的role就是null
                 */
                if (roleBuffer.get(previousRole.getKey()) == null){
                    roleBuffer.put(previousRole.getKey(), previousRole);
                }

                roleBuffer.get(role.getKey()).addAuthorities((Collection<Authority>) role.getAuthorities());
                continue;
            }
            previousRole = role;
            roleBuffer.put(role.getKey(), role);
        }
        return new ArrayList<>(roleBuffer.values());
    }

    /**
     * 将{@link Role}转换为{@link GrantedRole}, 当然, 这里是集合转换
     * @param handledRoles
     * @return
     */
    protected Collection<GrantedRole> transformToGrantedRole(Collection<Role> handledRoles){
        List<GrantedRole> grantedRoles = new ArrayList<>(handledRoles.size());
        handledRoles.stream().forEach(role -> grantedRoles.add(new DefaultGrantRole(role)));
        return grantedRoles;
    }

    @Override
    protected void initDao() throws Exception {
        Assert.notNull(getDataSource(), "数据源不能为空");
        Assert.notNull(separator, "分割器不能为空");
    }
}
