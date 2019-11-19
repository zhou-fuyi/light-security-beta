package com.light.security.core.exception;

/**
 * @ClassName TreeBuilderException
 * @Author ZhouJian
 * @Date 2019/11/19
 * @Description 树构建器异常
 */
public class TreeBuilderException extends InternalServiceException {
    public TreeBuilderException(Integer code, String msg) {
        super(code, msg);
    }
}
