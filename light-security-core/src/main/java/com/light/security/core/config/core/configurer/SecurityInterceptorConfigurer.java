package com.light.security.core.config.core.configurer;

import com.light.security.core.access.AccessDecisionVoter;
import com.light.security.core.access.vote.AdditionalVoter;
import com.light.security.core.access.vote.AuthorityVoter;
import com.light.security.core.config.core.builder.FilterChainBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SecurityInterceptorConfigurer<B extends FilterChainBuilder<B>> extends AbstractSecurityInterceptorConfigurer<SecurityInterceptorConfigurer<B>, B> {

    // TODO: 2019/12/14 预留出配置公共权限位置

    @Override
    protected List<AccessDecisionVoter<? extends Object>> getDecisionVoters(B builder) {
        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new LinkedList<>();
        AuthorityVoter authorityVoter= new AuthorityVoter();
        accessDecisionVoters.add(authorityVoter);
        AdditionalVoter additionalVoter = new AdditionalVoter();
        accessDecisionVoters.add(additionalVoter);
        return accessDecisionVoters;
    }
}
