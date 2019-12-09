package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.constant.AuthTypeEnum;
import com.light.security.core.properties.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @ClassName JdbcQuery
 * @Description 用于初始化JDBC使用查询SQL
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public class JdbcQuery implements InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SecurityProperties securityProperties;

    private final static Map<String, Map<String, String>> DEFAULT_QUERY = new HashMap<>();

    // SIMPLE_TYPE start
    private final static String SIMPLE_DEF_SUBJECTS_BY_SUBJECT_NAME_QUERY = "select id, subject_name subjectName, password, enabled from subject where subject_name = ?";
    private final static String SIMPLE_DEF_AUTHORITIES_BY_SUBJECT_ID_QUERY = "select" +
            " c.role_name roleName, c.role_code roleCode, c.role_desc roleDesc" +
            ", e.type type, e.parent_id parentId, e.authority_code `code`, e.authority_name `name`" +
            ", e.authority_desc `desc`, e.pattern pattern, e.authority_link link, e.authority_icon icon" +
            ", e.enabled enabled, e.opened opened" +
            " from (select * from subject where id = ?) a" +
            " left join subject_role b on a.id = b.subject_id" +
            " left join role c on b.role_id = c.id" +
            " left join role_authority d on c.id = d.role_id" +
            " left join authority e on d.authority_id = e.id";
    // SIMPLE_TYPE end
//    ===================================================================================================================================================================

    //ADVANCE_TYPE start
    private final static String ADVANCE_DEF_SUBJECTS_BY_SUBJECT_NAME_QUERY = "select id, subject_name subjectName, password, enabled from subject where subject_name = ?";

    /**
     * 查询账户管理的authority集合
     */
    private final static String ADVANCE_DEF_AUTHORITIES_BY_SUBJECT_ID_QUERY = "select" +
            " r.role_name roleName, r.role_code roleCode, r.role_desc roleDesc\n" +
            ", a.id id, a.type type" +
            " from (select * from subject where id = ?) s" +
            " left join subject_role sr on s.id = sr.subject_id" +
            " left join role r on sr.role_id = r.id" +
            " left join role_authority ra on r.id = ra.role_id" +
            " left join authority a on ra.authority_id = a.id";
    /**
     * 根据authority的id集合查询action
     */
    private final static String ADVANCE_DEF_ACTION_AUTHORITIES_QUERY = "select" +
            "  a.type type" +
            " , c.id id, c.parent_id parentId, c.action_code `code`, c.action_name `name`" +
            " , c.action_desc `desc`, c.pattern pattern, c.enabled enabled, c.opened opened" +
            "from (select * from authority where id in ?) a" +
            "left join authority_action b on a.id = b.authority_id" +
            "left join action c on b.action_id = c.id";

    private final static String ADVANCE_DEF_MENU_AUTHORITIES_QUERY = "select" +
            "  a.type type" +
            " , c.id id, c.parent_id parentId, c.menu_code `code`, c.menu_name `name`" +
            " , c.menu_desc `desc`, c.menu_link link, c.menu_icon icon" +
            " , c.enabled enabled, c.opened opened" +
            " from (select * from authority where id in ?) a" +
            " left join authority_menu b on a.id = b.authority_id" +
            " left join menu c on b.menu_id = c.id";

    private final static String ADVANCE_DEF_ELEMENT_AUTHORITIES_QUERY = "select" +
            "  a.type type" +
            " , c.id id, c.parent_id parentId, c.element_code `code`, c.element_name `name`" +
            " , c.element_desc `desc`, c.enabled enabled, c.opened opened" +
            " from (select * from authority where id in ?) a" +
            " left join authority_element b on a.id = b.authority_id" +
            " left join element c on b.element_id = c.id";
    //ADVANCE_TYPE end

    static {
        /**
         * 构建简单模式的权限查询
         */
        Map<String, String> SIMPLE_QUERY_MAP = new HashMap<>();
        SIMPLE_QUERY_MAP.put(QueryKey.DEF_SUBJECTS_BY_SUBJECT_NAME_QUERY.name(), SIMPLE_DEF_SUBJECTS_BY_SUBJECT_NAME_QUERY);
        SIMPLE_QUERY_MAP.put(QueryKey.DEF_AUTHORITIES_BY_SUBJECT_ID_QUERY.name(), SIMPLE_DEF_AUTHORITIES_BY_SUBJECT_ID_QUERY);
        DEFAULT_QUERY.put(AuthTypeEnum.SIMPLE.name(), SIMPLE_QUERY_MAP);

        /**
         * 构建进阶模式的权限查询
         */
        Map<String, String> ADVANCE_QUERY_MAP = new HashMap<>();
        ADVANCE_QUERY_MAP.put(QueryKey.DEF_SUBJECTS_BY_SUBJECT_NAME_QUERY.name(), ADVANCE_DEF_SUBJECTS_BY_SUBJECT_NAME_QUERY);
        ADVANCE_QUERY_MAP.put(QueryKey.DEF_AUTHORITIES_BY_SUBJECT_ID_QUERY.name(), ADVANCE_DEF_AUTHORITIES_BY_SUBJECT_ID_QUERY);
        ADVANCE_QUERY_MAP.put(QueryKey.DEF_ACTION_AUTHORITIES_QUERY.name(), ADVANCE_DEF_ACTION_AUTHORITIES_QUERY);
        ADVANCE_QUERY_MAP.put(QueryKey.DEF_MENU_AUTHORITIES_QUERY.name(), ADVANCE_DEF_MENU_AUTHORITIES_QUERY);
        ADVANCE_QUERY_MAP.put(QueryKey.DEF_ELEMENT_AUTHORITIES_QUERY.name(), ADVANCE_DEF_ELEMENT_AUTHORITIES_QUERY);
        DEFAULT_QUERY.put(AuthTypeEnum.ADVANCE.name(), ADVANCE_QUERY_MAP);
    }

    private static Map<String, String> CURRENT_QUERY = new HashMap<>();

    public JdbcQuery(){

    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initQuery();
    }

    /**
     * 用于初始化当前模式下使用SQL
     * 子类可以重写该方法进行实现
     */
    protected void initQuery() {
        CURRENT_QUERY = DEFAULT_QUERY.get(securityProperties.getAuthType().name());
        if (logger.isDebugEnabled()){
            logger.debug("当前模式为: {}, Query 数据有{}条",securityProperties.getAuthType().name(), CURRENT_QUERY.size());
            CURRENT_QUERY.forEach((key, query) -> logger.debug("key is {}, and the query is {}", key, query));
        }
    }

    public static String getQuery(String key){
        return CURRENT_QUERY.get(key);
    }

    public static enum QueryKey {
        DEF_SUBJECTS_BY_SUBJECT_NAME_QUERY,
        DEF_AUTHORITIES_BY_SUBJECT_ID_QUERY,

        DEF_ACTION_AUTHORITIES_QUERY,
        DEF_MENU_AUTHORITIES_QUERY,
        DEF_ELEMENT_AUTHORITIES_QUERY,
    }
}
