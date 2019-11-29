package com.light.security.core.exception;

import com.light.security.core.config.core.SecurityBuilder;

/**
 * @ClassName AlreadyBuiltException
 * @Description 表示目标对象已经被构建, 由 {@link SecurityBuilder#build()}方法调用时发生异常
 * @Author ZhouJian
 * @Date 2019-11-29
 */
public class AlreadyBuiltException extends IllegalStateException {

    public AlreadyBuiltException(String msg) {
        super(msg);
    }

}
