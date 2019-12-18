package com.light.security.core.access.vote;

import com.light.security.core.access.AccessDecisionVoter;
import com.light.security.core.access.AuthorityAttribute;
import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.access.FilterInvocation;
import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.token.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName AuthorityVoter
 * @Description 权限对比策略
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class AuthorityVoter implements AccessDecisionVoter<FilterInvocation> {

    private static final int DEFAULT_PER_ROLE_ACTION_AUTHORITY_SIZE = 30;
    private static Logger logger = LoggerFactory.getLogger(AuthorityVoter.class);

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof AuthorityAttribute;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation invocation, Collection<ConfigAttribute> attributes) {
        assert authentication != null;
        assert invocation != null;
        assert attributes != null;
        List<GrantedAuthority> authenticationGrantedAuthorities = findAuthenticationGrantedAuthority(authentication);
        //如果账户的API权限为空, 则弃权
        if (CollectionUtils.isEmpty(authenticationGrantedAuthorities)){
            return ACCESS_ABSTAIN;
        }
        int result = 0;
        boolean supported = false;
        for (ConfigAttribute attribute : attributes){
            if (this.supports(attribute)){
                supported = true;
                for (GrantedAuthority authority : authenticationGrantedAuthorities){
                    if (attribute.getAttribute().equals(authority.getAuthority().getAuthorityPoint())){
                        result ++;
                        break;
                    }
                }
            }
        }
        /**
         * 如果当前投票器不支持, 那么表示弃权
         */
        if (!supported){
            return ACCESS_ABSTAIN;
        }
        if (result > 0){
            if (logger.isDebugEnabled()){
                logger.debug("当前操作需要权限数量为: {}, 内容为: {}, 但是账户支持有数量: {}, 视为无权操作", attributes.size(), attributes, result);
            }
        }
        //如果当前操作需要权限数量大于1, 那么账户必须持有全部的权限才能授予访问权限, 否则视为无权操作
        return result == attributes.size() ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    // TODO: 2019-11-28 等待完善
    /**
     * 从当前的Authentication中获取API权限数据
     *
     * 系统内暂时不支持rememberMe 和 anonymous
     * @param authentication
     * @return
     */
    private List<GrantedAuthority> findAuthenticationGrantedAuthority(Authentication authentication){

        if (!CollectionUtils.isEmpty(authentication.getGrantedRoles())){
            Collection<GrantedRole> grantedRoles = (Collection<GrantedRole>) authentication.getGrantedRoles();
            List<GrantedAuthority> grantedActionAuthorities = new ArrayList<>(grantedRoles.size() * DEFAULT_PER_ROLE_ACTION_AUTHORITY_SIZE);
            Iterator<GrantedRole> grantedRoleIterator = grantedRoles.iterator();
            while (grantedRoleIterator.hasNext()){
                grantedActionAuthorities.addAll(grantedRoleIterator.next().findActionAuthorities());
            }
            return new ArrayList<>(grantedActionAuthorities);
        }
        return null;
    }
}
