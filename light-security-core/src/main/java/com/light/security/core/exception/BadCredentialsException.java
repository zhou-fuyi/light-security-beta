package com.light.security.core.exception;

/**
 * @ClassName BadCredentialsException
 * @Description 由于账户凭据错误(常为密码错误)而拒绝进行身份验证请求, 则抛出此异常
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class BadCredentialsException extends AuthenticationException {

    public BadCredentialsException(Integer code, String msg) {
        super(code, msg);
    }
}
