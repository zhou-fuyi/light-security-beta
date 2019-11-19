package com.light.security.exception;

/**
 * @ClassName CredentialsExpiredException
 * @Description 由于账户凭据已过期而拒绝身份验证请求, 则抛出此异常
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class CredentialsExpiredException extends AccountStatusException {

    public CredentialsExpiredException(Integer code, String msg) {
        super(code, msg);
    }
}
