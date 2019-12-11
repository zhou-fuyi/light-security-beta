package com.light.security.core.authentication.dao.jdbc;

import com.light.security.core.access.model.AssistAuthority;
import com.light.security.core.access.model.Authority;
import com.light.security.core.access.model.Role;
import com.light.security.core.access.model.SimpleAssistAuthority;
import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.dao.jdbc.auth.AuthorityDaoProcessor;
import com.light.security.core.exception.ProcessorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName AbstractAdditionJdbcProcessor
 * @Description 用于除简易模式外的权限获取, 存在权限类型划分并分表存储的模式
 * @Author ZhouJian
 * @Date 2019-12-11
 */
public abstract class AbstractAdditionJdbcProcessor extends AbstractJdbcProcessor {

    @Autowired
    private List<AuthorityDaoProcessor> authorityDaoProcessors;


    @Override
    protected Collection<GrantedRole> comparatorAndTransformToGrantedRoleList(Collection<Role> roles) {
        roles = comparatorAndRepeatMergeRoleList(roles);
        for (Role role : roles){
            Collection<AssistAuthority> assistAuthorities = (Collection<AssistAuthority>) role.getAuthorities();
            role.setAuthorities(loadSubAuthorities(assistAuthorities));
        }
        return transformToGrantedRole(roles);
    }

    /**
     * 批量加载子权限, 或者说加载分类权限
     * @param assistAuthorities
     * @return
     */
    protected List<Authority> loadSubAuthorities(Collection<AssistAuthority> assistAuthorities){
        List<Authority> authorities = new ArrayList<>(assistAuthorities.size());
        Map<String, List<AssistAuthority>> groupByTypeAuthorityListMap = assistAuthorities.stream().collect(Collectors.groupingBy(AssistAuthority::getAuthorityType));
        for (Map.Entry<String, List<AssistAuthority>> entry : groupByTypeAuthorityListMap.entrySet()){
            boolean processed = false;
            for (AuthorityDaoProcessor authorityDaoProcessor : authorityDaoProcessors){
                if (authorityDaoProcessor.support(entry.getKey())){
                    authorities.addAll(authorityDaoProcessor.loadAuthorities(entry.getValue()));
                    processed = true;
                    break;
                }
            }
            if (!processed){
                if (logger.isDebugEnabled()){
                    logger.debug("未找到适配当前权限类型为: {}的权限处理器", entry.getKey());
                }
                throw new ProcessorNotFoundException(500, "找不到适配权限类型为" + entry.getKey() + "的权限处理器");
            }
        }
        return authorities;
    }


}
