package com.light.security.core.exception;

/**
 * @ClassName DisabledException
 * @Description 账户被禁用
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class DisabledException extends AccountStatusException {
    public DisabledException(Integer code, String msg) {
        super(code, msg);
    }
}
