package com.light.security.core.access.vote;

import com.light.security.core.access.AccessDecisionVoter;
import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.authentication.token.AnonymousAuthenticationToken;
import com.light.security.core.authentication.token.Authentication;

import java.util.Collection;

/**
 * @ClassName AdditionalVoter
 * @Description 用于处理一些额外定义的权限比对
 * @Author ZhouJian
 * @Date 2019-12-18
 */
public class AdditionalVoter implements AccessDecisionVoter<Object> {

    private static final String AUTHENTICATED = "_authenticated";

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute.getAttribute().equals(AUTHENTICATED);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    /**
     * 目前只对匿名用户进行支持
     * @param authentication
     * @param object
     * @param configAttributes
     * @return
     */
    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {
        int result = ACCESS_ABSTAIN;
        for (ConfigAttribute attribute : configAttributes){
            if (this.supports(attribute)){
                return ACCESS_DENIED;
            }
        }
        return result;
    }
}
