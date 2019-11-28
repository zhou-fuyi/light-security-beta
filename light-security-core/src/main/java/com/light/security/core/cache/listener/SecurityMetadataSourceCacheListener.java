package com.light.security.core.cache.listener;

import com.light.security.core.access.AuthorityAttribute;
import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.meta.DefaultFilterInvocationSecurityMetadataSource;
import com.light.security.core.access.model.ActionAuthority;
import com.light.security.core.access.model.Authority;
import com.light.security.core.cache.holder.SecurityMetadataSourceContextCacheHolder;
import com.light.security.core.util.matcher.AntPathRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.util.Arrays;
import java.util.Collection;

/**
 * @ClassName SecurityMetadataSourceCacheListener
 * @Description 用于加载<code>SecurityMetadataSource</code>, 并将其存放于<code>SecurityMetadataSourceCache</code>中
 * @Author ZhouJian
 * @Date 2019-11-28
 */
@WebListener
public class SecurityMetadataSourceCacheListener extends AbstractCacheContextListener {

    @Autowired
    private SecurityMetadataSourceContextCacheHolder securityMetadataSourceContextCacheHolder;


    @Override
    protected void loadCache(ServletContextEvent servletContextEvent) {
        logger.warn("请实现自己的权限元数据的加载逻辑");
    }

    private void transformToSecurityMetadataSources(Collection<Authority> authorities){
        if (!CollectionUtils.isEmpty(authorities)){
            authorities.forEach(authority -> {
                securityMetadataSourceContextCacheHolder.put(getRequestMatcher(authority), Arrays.asList(new AuthorityAttribute(authority.getAuthorityPoint())));
            });
        }
    }

    private RequestMatcher getRequestMatcher(Authority authority){
        ActionAuthority actionAuthority = (ActionAuthority) authority;
        RequestMatcher requestMatcher = new AntPathRequestMatcher(actionAuthority.getPattern());
        return requestMatcher;
    }
}
