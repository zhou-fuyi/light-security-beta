package com.light.security.exception;

/**
 * @ClassName NoAccessException
 * @Description 由于授权时, 账户没有权限而拒绝下一步访问, 则抛出此异常.
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class NoAccessException extends AccessDeniedException {

    public NoAccessException(Integer code, String msg) {
        super(code, msg);
    }
}
