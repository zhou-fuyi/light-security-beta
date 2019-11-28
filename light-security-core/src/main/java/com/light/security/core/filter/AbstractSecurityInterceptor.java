package com.light.security.core.filter;

import com.light.security.core.access.AccessDecisionManager;
import com.light.security.core.access.AuthorizationStatusToken;
import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.access.event.AuthenticationCredentialsNotFoundEvent;
import com.light.security.core.access.event.AuthorizationFailureEvent;
import com.light.security.core.access.event.AuthorizationSuccessEvent;
import com.light.security.core.access.event.PublicInvocationEvent;
import com.light.security.core.access.meta.SecurityMetadataSource;
import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AccessDeniedException;
import com.light.security.core.exception.AuthenticationCredentialsNotFoundException;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.AuthenticationServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @ClassName AbstractSecurityInterceptor
 * @Description 权限验证抽象类
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public abstract class AbstractSecurityInterceptor implements InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher eventPublisher;

    private SecurityContextHolder securityContextHolder;

    /**
     * 访问控制决策管理器
     */
    private AccessDecisionManager accessDecisionManager;

    //始终重新认证
    private boolean alwaysReauthenticate = false;

    //是否发布授权成功事件
    private boolean publishAuthorizationSuccess = true;

    private AuthenticationManager authenticationManager;

    /**
     * 检查authentication的状态, 并且根据 alwaysReauthenticate 判断是否重新认证
     * 如果 authentication.isAuthenticated() 为false或者是 alwaysReauthenticate 为true
     * 都会进行重新认证
     * @return
     */
    private Authentication authenticateIfRequired() {
        Authentication authentication = securityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !this.alwaysReauthenticate){
            if (logger.isDebugEnabled()) {
                logger.debug("Previously Authenticated: " + authentication);//先前已经认证
            }

            return authentication;
        }

        authentication = authenticationManager.authenticate(authentication);//重新认证
        // We don't authenticated.setAuthentication(true), because each provider should do that
        // 这不是必须的嘛, 当然不能直接改变状态咯
        if (logger.isDebugEnabled()) {
            logger.debug("Successfully Authenticated: " + authentication);
        }
        securityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    /**
     * 无法从SecurityContext中获取认证对象Authentication处理方案
     * @param reason
     * @param object
     * @param attributes
     */
    private void credentialsNotFound(String reason, Object object, Collection<ConfigAttribute> attributes){
        AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException(401, reason);
        AuthenticationCredentialsNotFoundEvent event = new AuthenticationCredentialsNotFoundEvent(object, attributes, exception);
        publishEvent(event);
        throw exception;
    }

    private void publishEvent(ApplicationEvent event){
        if (this.eventPublisher != null){
            this.eventPublisher.publishEvent(event);
        }
    }

    /**
     * invocation前置处理 主要用于权限校验
     * @param object
     * @return
     */
    protected AuthorizationStatusToken beforeInvocation(Object object){
        Assert.notNull(object, "Object is null");
        boolean debug = logger.isDebugEnabled();
        if (!getSecureObjectClass().isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException(
                    "Security invocation attempted for object "
                            + object.getClass().getName()
                            + " but AbstractSecurityInterceptor only configured to support secure objects of type: "
                            + getSecureObjectClass());
        }
        Collection<ConfigAttribute> attributes = this.obtainSecurityMetadataSource().getAttributes(object);
        if (CollectionUtils.isEmpty(attributes)){
            if (debug) {
                logger.debug("公共安全对象, 未尝试认证, 当前请求匹配权限为空");
            }
            publishEvent(new PublicInvocationEvent(object));
            return null;
        }

        if (debug){
            logger.debug("Secure object: " + object + "; Attributes: " + attributes);
        }

        if (securityContextHolder.getContext().getAuthentication() == null){
            credentialsNotFound("An Authentication object was not found in the SecurityContext", object, attributes);
        }

        Authentication authenticated = authenticateIfRequired();
        try {
            this.accessDecisionManager.decide(authenticated, object, attributes);
        }catch (AccessDeniedException accessDeniedException){
            //授权失败事件发布
            publishEvent(new AuthorizationFailureEvent(object, attributes, authenticated, accessDeniedException));
            //授权失败异常抛出
            throw accessDeniedException;
        }

        if (debug) {
            logger.debug("Authorization successful");
        }

        // 在这里不进行runAsManager的逻辑, 所以直接发布了成功授权事件
        if (publishAuthorizationSuccess) {
            publishEvent(new AuthorizationSuccessEvent(object, attributes, authenticated));
        }
        return new AuthorizationStatusToken(securityContextHolder.getContext(), attributes, object);
    }

    /**
     * 这里原本就是结合runAsManager逻辑的处理, 如果runAsManager改变了SecurityContext中的值, 那么在这里进行恢复
     * 虽然我没有这个业务, 不过暂时留着作为一个生命周期函数呗, 又不花钱, 哈哈
     * @param token
     */
    protected void finallyInvocation(AuthorizationStatusToken token){
        if (logger.isDebugEnabled()){
            logger.debug("我啥也没有做啊");
        }
    }

    /**
     * 作为invocation的后处理器 暂时没有这方面的业务
     * @param token
     * @param returnObject
     * @return
     */
//    protected Object afterInvocation(AuthorizationStatusToken token, Object returnObject){
//
//    }


    /**
     * 获取权限元数据源类对象
     * @return
     */
    protected abstract SecurityMetadataSource obtainSecurityMetadataSource();

    public abstract Class<?> getSecureObjectClass();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(accessDecisionManager, "AccessDecisionManager 不能为 null");
        Assert.notNull(authenticationManager, "AuthenticationManager 不能为 null");
        Assert.notNull(obtainSecurityMetadataSource(), "SecurityMetadataSource 不能为 null");

        Assert.isTrue(accessDecisionManager.supports(getSecureObjectClass()), "AuthenticationManager 不支持类型: " + getSecureObjectClass());
        Assert.isTrue(obtainSecurityMetadataSource().supports(getSecureObjectClass()), "SecurityMetadataSource 不支持类型: " + getSecureObjectClass());
    }

    /**
     * 默认实现, 就是不进行重新认证
     */
    private static class NoOpAuthenticationManager implements AuthenticationManager {

        private static Logger logger = LoggerFactory.getLogger(NoOpAuthenticationManager.class);

        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            if (logger.isDebugEnabled()){
                logger.debug("尝试重新认证, 请查看导致重新认证的缘由是由于未认证还是alwaysReauthenticate, 默认实现会抛出AuthenticationServiceException异常");
            }
            throw new AuthenticationServiceException(500, "Cannot authenticate " + authentication);
        }
    }
}
