package com.light.security.core.exception;

/**
 * @ClassName LockedException
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class LockedException extends AccountStatusException {
    public LockedException(Integer code, String msg) {
        super(code, msg);
    }
}
