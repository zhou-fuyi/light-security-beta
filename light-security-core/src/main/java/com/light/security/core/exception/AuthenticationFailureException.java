package com.light.security.core.exception;

/**
 * @ClassName AuthenticationFailureException
 * @Description 认证失败时, 抛出此异常.
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class AuthenticationFailureException extends AuthenticationException {

    public AuthenticationFailureException(Integer code, String msg) {
        super(code, msg);
    }
}
