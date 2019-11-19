package com.light.security.exception;

/**
 * @ClassName AccessDeniedException
 * @Description 拒绝访问异常，其下分为授权过程的可能发生的异常
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public abstract class AccessDeniedException extends LightSecurityException {

    public AccessDeniedException(Integer code, String msg) {
        super(code, msg);
    }
}
