package com.light.security.core.exception;

/**
 * @ClassName LogException
 * @Description 日志相关异常信息承载顶层类
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class LogException extends LightSecurityException {

    public LogException(Integer code, String msg) {
        super(code, msg);
    }
}
