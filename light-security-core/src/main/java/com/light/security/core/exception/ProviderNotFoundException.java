package com.light.security.core.exception;

/**
 * @ClassName ProviderNotFoundException
 * @Description 如果找不到支持 {@link com.light.security.core.authentication.token.Authentication}的
 * {@link com.light.security.core.access.AuthenticationProvider}就抛出此异常
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class ProviderNotFoundException extends AuthenticationException {

    public ProviderNotFoundException(Integer code, String msg) {
        super(code, msg);
    }
}
