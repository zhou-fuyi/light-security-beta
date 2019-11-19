package com.light.security.core.exception;

/**
 * @ClassName InternalServiceException
 * @Author ZhouJian
 * @Date 2019/11/19
 * @Description 内部服务异常
 */
public abstract class InternalServiceException extends LightSecurityException {
    public InternalServiceException(Integer code, String msg) {
        super(code, msg);
    }
}
