package com.light.security.exception;

/**
 * @ClassName AccountStatusException
 * @Description 由特定用户状态(锁定、禁用、过期)引起的身份验证异常基类
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public abstract class AccountStatusException extends AuthenticationException{

    public AccountStatusException(Integer code, String msg) {
        super(code, msg);
    }
}
