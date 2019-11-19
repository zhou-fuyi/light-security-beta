package com.light.security.exception;

/**
 * @ClassName AuthenticationServiceException
 * @Description 当认证过程中, 认证涉及的相关service层出现异常时, 抛出此异常.
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public abstract class AuthenticationServiceException extends AuthenticationException {

    public AuthenticationServiceException(Integer code, String msg) {
        super(code, msg);
    }
}
