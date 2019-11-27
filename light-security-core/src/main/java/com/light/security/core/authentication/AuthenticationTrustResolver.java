package com.light.security.core.authentication;

import com.light.security.core.authentication.token.Authentication;

/**
 * @InterfaceName AuthenticationTrustResolver
 * @Description 仿照 SpringSecurity 完成
 * Evaluates <code>Authentication</code> tokens
 *
 * 翻译:
 * 评估<code> Authentication </ code>令牌, 用于判断令牌的来源(匿名用户|RememberMe|Authenticate)
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public interface AuthenticationTrustResolver {

    /**
     * Indicates whether the passed <code>Authentication</code> token represents an
     * anonymous user. Typically the framework will call this method if it is trying to
     * decide whether an <code>AccessDeniedException</code> should result in a final
     * rejection (i.e. as would be the case if the principal was non-anonymous/fully
     * authenticated) or direct the principal to attempt actual authentication (i.e. as
     * would be the case if the <code>Authentication</code> was merely anonymous).
     *
     *
     * 翻译:
     * 指示所传递的<code> Authentication </ code>令牌是否代表匿名用户。
     * 通常，如果框架试图确定<code> AccessDeniedException </ code>是否
     * 应导致最终拒绝（即，如果主体是非匿名/完全经过身份验证的情况）或直接引导框架，
     * 则框架将调用此方法。 委托人尝试进行实际身份验证
     * （例如，如果<code> Authentication </ code>仅是匿名的，将是这种情况）。
     * @param authentication to test (may be <code>null</code> in which case the method
     * will always return <code>false</code>)
     *
     * @return <code>true</code> the passed authentication token represented an anonymous
     * principal, <code>false</code> otherwise
     */
    boolean isAnonymous(Authentication authentication);

    /**
     * Indicates whether the passed <code>Authentication</code> token represents user that
     * has been remembered (i.e. not a user that has been fully authenticated).
     * <p>
     * The method is provided to assist with custom <code>AccessDecisionVoter</code>s and
     * the like that you might develop. Of course, you don't need to use this method
     * either and can develop your own "trust level" hierarchy instead.
     *
     *
     * 翻译:
     * 指示所传递的<code> Authentication </ code>令牌是否表示已被记住的用户（即不是经过完全认证的用户）。
     * <p>
     * 提供该方法是为了帮助您开发自定义的<code> AccessDecisionVoter </ code>及其类似内容。
     * 当然，您也不需要使用此方法，而是可以开发自己的“信任级别”层次结构。
     * @param authentication to test (may be <code>null</code> in which case the method
     * will always return <code>false</code>)
     *
     * @return <code>true</code> the passed authentication token represented a principal
     * authenticated using a remember-me token, <code>false</code> otherwise
     */
    boolean isRememberMe(Authentication authentication);
}
