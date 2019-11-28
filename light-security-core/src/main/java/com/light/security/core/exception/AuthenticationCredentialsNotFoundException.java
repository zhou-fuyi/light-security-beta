package com.light.security.core.exception;

/**
 * @ClassName AuthenticationCredentialsNotFoundException
 * @Description 仿照SpringSecurity完成
 * Thrown if an authentication request is rejected because there is no
 * {@link com.light.security.core.authentication.token.Authentication}
 * object in the {@link com.light.security.core.authentication.context.SecurityContext SecurityContext}.
 *
 * 小声BB:
 * 如果在授权流程中在{@link com.light.security.core.authentication.context.SecurityContext}中
 * 获取不到{@link com.light.security.core.authentication.token.Authentication} 对象, 则抛出此异常
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class AuthenticationCredentialsNotFoundException extends AuthenticationException {

    public AuthenticationCredentialsNotFoundException(Integer code, String msg) {
        super(code, msg);
    }
}
