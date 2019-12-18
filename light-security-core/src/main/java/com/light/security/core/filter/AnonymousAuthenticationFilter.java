package com.light.security.core.filter;

import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.AuthenticationDetailsSource;
import com.light.security.core.authentication.WebAuthenticationDetailsSource;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.token.AnonymousAuthenticationToken;
import com.light.security.core.authentication.token.Authentication;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName AnonymousAuthenticationFilter
 * @Description 匿名认证过滤器
 * @Author ZhouJian
 * @Date 2019-12-18
 */
public class AnonymousAuthenticationFilter extends GenericFilter implements InitializingBean {

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private String key;
    private Object principal;
    private List<GrantedRole> roles;
    private SecurityContextHolder securityContextHolder;


    public AnonymousAuthenticationFilter(String key, SecurityContextHolder securityContextHolder){
        this(key, "AnonymousSubject", Collections.emptyList(), securityContextHolder);
    }

    public AnonymousAuthenticationFilter(String key, Object principal, List<GrantedRole> roles, SecurityContextHolder securityContextHolder){
        Assert.hasLength(key, "构造器不接受空值参数 --> key is null or empty");
        Assert.notNull(principal, "构造器不接受空值参数 --> principal is null");
        Assert.notNull(roles, "构造器不接受空值参数 --> roles is null");
        Assert.notNull(securityContextHolder, "构造器不接受空值参数 --> securityContextHolder is null");
        this.key = key;
        this.principal = principal;
        this.roles = roles;
        this.securityContextHolder = securityContextHolder;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (securityContextHolder.getContext().getAuthentication() == null){
            securityContextHolder.getContext().setAuthentication(createAuthentication((HttpServletRequest) request));

            if (logger.isDebugEnabled()) {
                logger.debug("使用 匿名令牌: '" + securityContextHolder.getContext().getAuthentication() + "' 填充SecurityContextHolder");
            }
        }else {
            if (logger.isDebugEnabled()) {
                logger.debug("SecurityContextHolder已经包含: '" + securityContextHolder.getContext().getAuthentication() + "', 不再进行匿名令牌的填充");
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 创建一个匿名令牌对象
     * @param request
     * @return
     */
    protected Authentication createAuthentication(HttpServletRequest request){
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken(key, principal, roles);
        anonymous.setDetails(authenticationDetailsSource.buildDetails(request));
        return anonymous;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource 不能为空");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(key, "必须是有效值 --> key must have length");
        Assert.notNull(principal, "必须是有效值 --> Anonymous authentication principal must be set");
        Assert.notNull(roles, "Anonymous roles must be set");
        Assert.notNull(securityContextHolder, "securityContextHolder is null");
    }
}
