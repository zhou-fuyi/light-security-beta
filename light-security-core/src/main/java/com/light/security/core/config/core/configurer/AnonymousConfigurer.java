package com.light.security.core.config.core.configurer;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.AnonymousAuthenticationProvider;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.token.AnonymousAuthenticationToken;
import com.light.security.core.config.core.builder.FilterChainBuilder;
import com.light.security.core.filter.AnonymousAuthenticationFilter;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName AnonymousConfigurer
 * @Description 匿名过滤器配置器
 * @Author ZhouJian
 * @Date 2019-12-18
 */
public class AnonymousConfigurer<B extends FilterChainBuilder<B>> extends AbstractHttpConfigurer<AnonymousConfigurer<B>, B> {

    private String key;
    private AuthenticationProvider authenticationProvider;
    private AnonymousAuthenticationFilter anonymousAuthenticationFilter;
    private Object principle = "AnonymousSubject";
    private List<GrantedRole> roles = Collections.emptyList();

    private SecurityContextHolder securityContextHolder;

    public AnonymousConfigurer(SecurityContextHolder securityContextHolder){
        Assert.notNull(securityContextHolder, "构造器不接受空值参数 --> securityContextHolder is null");
        this.securityContextHolder = securityContextHolder;
    }

    public AnonymousConfigurer<B> key(String key){
        this.key = key;
        return this;
    }

    public AnonymousConfigurer<B> principle(Object principle){
        this.principle = principle;
        return this;
    }

    public AnonymousConfigurer<B> roles(List<GrantedRole> roles){
        this. roles = roles;
        return this;
    }

    public AnonymousConfigurer<B> authenticationProvider(AuthenticationProvider authenticationProvider){
        this.authenticationProvider = authenticationProvider;
        return this;
    }

    public AnonymousConfigurer<B> securityContextHolder(SecurityContextHolder securityContextHolder){
        Assert.notNull(securityContextHolder, "securityContextHolder 不能为 null");
        this.securityContextHolder = securityContextHolder;
        return this;
    }

    @Override
    public void init(B builder) throws Exception {
        if (authenticationProvider == null){
            authenticationProvider = new AnonymousAuthenticationProvider(getKey());
        }
        if (anonymousAuthenticationFilter == null){
            anonymousAuthenticationFilter = new AnonymousAuthenticationFilter(getKey(), principle, roles, securityContextHolder);
        }
        authenticationProvider = postProcess(authenticationProvider);
        builder.authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(B builder) throws Exception {
        anonymousAuthenticationFilter.afterPropertiesSet();
        builder.addFilter(anonymousAuthenticationFilter);
    }

    /**
     * 可以是一个恒定的值, 也可以是一个随机值(但是系统启动之后也是恒定的, 至少在系统正常运行期间)
     * @return
     */
    private String getKey(){
        if (key == null){
            key = UUID.randomUUID().toString();
        }
        return key;
    }
}
