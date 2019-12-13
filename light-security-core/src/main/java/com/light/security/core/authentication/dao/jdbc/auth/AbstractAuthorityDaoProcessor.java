package com.light.security.core.authentication.dao.jdbc.auth;

import com.light.security.core.access.model.AssistAuthority;
import com.light.security.core.access.model.Authority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName AbstractAuthorityDaoProcessor
 * @Description 估计以后可能会用到, 目前我就放一个日志
 * @Author ZhouJian
 * @Date 2019-12-11
 */
public abstract class AbstractAuthorityDaoProcessor extends JdbcDaoSupport implements AuthorityDaoProcessor {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final String supportAuthorityType;

    protected AbstractAuthorityDaoProcessor(DataSource dataSource, String supportAuthorityType){
        if (dataSource == null ||StringUtils.isEmpty(supportAuthorityType)){
            throw new IllegalArgumentException("构造器不接受空值参数 --> jdbcTemplate is null or supportAuthorityType is empty");
        }
        setDataSource(dataSource);
        this.supportAuthorityType = supportAuthorityType;
    }

    @Override
    public Authority loadAuthority(AssistAuthority assistAuthority) {
        List<Authority> authorities = loadAuthorities(Arrays.asList(assistAuthority));
        return CollectionUtils.isEmpty(authorities) ? null : authorities.get(0);
    }

    /**
     * 进行权限获取的一些后处理, 目前主要是处理 enabled = false 的情况, 在{@link ActionAuthorityDaoProcessor#loadAuthorities(List)},
     * {@link MenuAuthorityDaoProcessor#loadAuthorities(List)}, {@link ElementAuthorityDaoProcessor#loadAuthorities(List)}方法返回前都会先调用该方法
     * @param authorities
     * @return
     */
    protected void postHandler(List<Authority> authorities){
        if (!CollectionUtils.isEmpty(authorities)){
            Iterator<Authority> iterator = authorities.iterator();
            while (iterator.hasNext()){
                Authority authority = iterator.next();
                if (!authority.isEnabled()){
                    if (logger.isDebugEnabled()){
                        logger.debug("当前权限[{}]为不可以用权限, 将会自动去除", authority);
                    }
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public boolean support(String type) {
        return supportAuthorityType.equals(type.toLowerCase());
    }
}
