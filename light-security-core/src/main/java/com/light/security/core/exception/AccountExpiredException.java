package com.light.security.core.exception;

/**
 * @ClassName AccountExpiredException
 * @Description 账户已过期
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class AccountExpiredException extends AccountStatusException{

    public AccountExpiredException(Integer code, String msg) {
        super(code, msg);
    }
}
