package com.light.security.core.authentication.dao.jdbc.auth;

import com.light.security.core.access.model.AssistAuthority;
import com.light.security.core.access.model.Authority;

import java.util.List;

/**
 * @InterfaceName AuthorityDaoProcessor
 * @Description 用于加载权限的处理器, 因为存在不同类型的权限, 又不想写 switch case
 * @Author ZhouJian
 * @Date 2019-12-11
 */
public interface AuthorityDaoProcessor {

    /**
     * 用于加载权限
     * @param assistAuthority
     * @return
     */
    Authority loadAuthority(AssistAuthority assistAuthority);

    /**
     * 批量获取
     * @param assistAuthorities
     * @return
     */
    List<Authority> loadAuthorities(List<AssistAuthority> assistAuthorities);

    /**
     * 用于判断该处理器是否支持当前权限类型
     * @param type
     * @return
     */
    boolean support(String type);

}
