package com.light.security.core.filter;

import com.light.security.core.authentication.AuthenticationDetailsSource;
import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.WebAuthenticationDetailsSource;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.event.InteractiveAuthenticationSuccessEvent;
import com.light.security.core.authentication.handler.AuthenticationFailureHandler;
import com.light.security.core.authentication.handler.AuthenticationSuccessHandler;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.InternalAuthenticationServiceException;
import com.light.security.core.util.ServletUtils;
import com.light.security.core.util.matcher.AntPathRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName AbstractAuthenticationFilter
 * @Description 认证过滤器的通用实现
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public abstract class AbstractAuthenticationFilter extends GenericFilter{

    //用于构建以及存放认证时产生的Details信息
    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    //认证管理器
    private AuthenticationManager authenticationManager;

    //认证成功处理器
    private AuthenticationSuccessHandler successHandler;

    //认证失败处理器
    private AuthenticationFailureHandler failureHandler;

    //事件发布
    protected ApplicationEventPublisher eventPublisher;

    protected SecurityContextHolder securityContextHolder;

    protected AbstractAuthenticationFilter(RequestMatcher matcher){
        super(matcher);
    }

    @Override
    protected void genericInit() {
        Assert.notNull(authenticationManager, "authenticationManager 不能为null");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (!requireAuthentication(request)){
            chain.doFilter(request, response);
            return;
        }
        if (logger.isDebugEnabled()){
            logger.debug("当前请求需要进行认证");
        }

        Authentication authResult = null;
        try {
            authResult = attemptAuthentication(request, response);
            if (null == authResult){
                return;
            }
        }catch (InternalAuthenticationServiceException failed){
            logger.error("出现内部认证服务异常: {}", failed.getMessage());
            unsuccessfulAuthentication(request, response, failed);
            return;
        }catch (AuthenticationException failed){
            unsuccessfulAuthentication(request, response, failed);
            return;
        }
        successfulAuthentication(request, response, chain, authResult);
    }

    /**
     * 认证成功预处理方法
     * @param request
     * @param response
     * @param chain
     * @param authResult
     */
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException{
        if (logger.isDebugEnabled()){
            logger.debug("认证成功, 即将更新SecurityContext: {}", authResult);
        }
        securityContextHolder.getContext().setAuthentication(authResult);
        if (eventPublisher != null){
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    /**
     * 认证失败预处理方法
     * @param request
     * @param response
     * @param failed
     */
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException{
        securityContextHolder.cleanContext();
        if (logger.isDebugEnabled()){
            logger.debug("认证失败, 已将SecurityContext清空");
        }
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    /**
     * 判断过滤器是否需要作用于当前请求
     * @param request
     * @return
     */
    private boolean requireAuthentication(HttpServletRequest request){
        return getMatcher().matches(request);
    }

    /**
     * 尝试进行认证, 是认证逻辑实现部分
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    protected abstract Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException;

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    public AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    public ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "authenticationDetailsSource 是必须存在的");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    public void setSecurityContextHolder(SecurityContextHolder securityContextHolder) {
        this.securityContextHolder = securityContextHolder;
    }
}
