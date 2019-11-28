package com.light.security.core.exception;

/**
 * @ClassName InsufficientAuthenticationException
 * @Description 仿照SpringSecurity完成
 * Thrown if an authentication request is rejected because the credentials are not
 * sufficiently trusted.
 * <p>
 * {@link org.springframework.security.access.AccessDecisionVoter}s will typically throw
 * this exception if they are dissatisfied with the level of the authentication, such as
 * if performed using a remember-me mechanism or anonymously. The
 * {@code ExceptionTranslationFilter} will then typically cause the
 * {@code AuthenticationEntryPoint} to be called, allowing the principal to authenticate
 * with a stronger level of authentication.
 *
 *
 * 翻译:
 * 如果由于凭据未充分信任而拒绝身份验证请求，则抛出该异常。
 * <p>
 * {@link org.springframework.security.access.AccessDecisionVoter}
 * 如果<code> AccessDecisionVoter </code>对身份验证级别不满意（例如使用“记住我”机制或匿名执行），通常会引发此异常。
 * {@code ExceptionTranslationFilter}通常会调用{@code AuthenticationEntryPoint}，通知委托人以更强的身份验证级别进行身份验证。
 *
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public class InsufficientAuthenticationException extends AuthenticationException {

    public InsufficientAuthenticationException(Integer code, String msg) {
        super(code, msg);
    }
}
