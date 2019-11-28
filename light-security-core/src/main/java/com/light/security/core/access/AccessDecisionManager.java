package com.light.security.core.access;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AccessDeniedException;
import com.light.security.core.exception.InsufficientAuthenticationException;

import java.util.Collection;

/**
 * @InterfaceName AccessDecisionManager
 * @Description 仿照SpringSecurity完成
 * Makes a final access control (authorization) decision.
 *
 *
 * 翻译:
 * 做出最终访问控制(授权)决定
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public interface AccessDecisionManager {

    /**
     * 进行访问控制决策
     * @param authentication the caller invoking the method (not null)
     * @param object the secured object being called
     * @param configAttributes the configuration attributes associated with the secured object being invoked
     * @throws AccessDeniedException if access is denied as the authentication does not hold a required authority or ACL privilege
     * @throws InsufficientAuthenticationException if access is denied as the authentication does not provide a sufficient level of trust
     */
    void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException;

    /**
     * Indicates whether this <code>AccessDecisionManager</code> is able to process
     * authorization requests presented with the passed <code>ConfigAttribute</code>.
     * <p>
     * This allows the <code>AbstractSecurityInterceptor</code> to check every
     * configuration attribute can be consumed by the configured
     * <code>AccessDecisionManager</code> and/or <code>RunAsManager</code> and/or
     * <code>AfterInvocationManager</code>.
     * </p>
     *
     *
     * 翻译:
     * 判断此<code> AccessDecisionManager </ code>是否能够处理通过传递的<code> ConfigAttribute </ code>提出的授权请求。
     * <p>
     * 这允许<code> AbstractSecurityInterceptor </ code>检查每个配置属性可以被配置
     * <code> AccessDecisionManager </ code>和/或<code> RunAsManager </ code>和/或<code> AfterInvocationManager </ code>。
     * @param attribute
     * @return
     */
    boolean supports(ConfigAttribute attribute);

    /**
     * Indicates whether the <code>AccessDecisionManager</code> implementation is able to
     * provide access control decisions for the indicated secured object type.
     *
     *
     * 翻译:
     * 判断<code> AccessDecisionManager </ code>实现是否能够为指示的安全对象类型提供访问控制决策。
     * @param clazz
     * @return
     */
    boolean supports(Class<?> clazz);

}
