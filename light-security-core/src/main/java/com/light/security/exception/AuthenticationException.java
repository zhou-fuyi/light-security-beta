package com.light.security.exception;

/**
 * @ClassName AuthenticationException
 * @Description 认证相关异常顶层类
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public abstract class AuthenticationException extends LightSecurityException {

    public AuthenticationException(Integer code, String msg) {
        super(code, msg);
    }
}
