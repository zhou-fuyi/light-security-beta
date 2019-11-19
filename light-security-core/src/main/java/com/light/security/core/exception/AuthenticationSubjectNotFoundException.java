package com.light.security.core.exception;

/**
 * @ClassName AuthenticationSubjectNotFoundException
 * @Description 从<code>SecurityContext</code>中获取不到Authentication对象时, 抛出此异常.
 * 通常用于授权过程中获取并决定是否抛出.
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class AuthenticationSubjectNotFoundException extends AuthenticationException {

    public AuthenticationSubjectNotFoundException(Integer code, String msg) {
        super(code, msg);
    }
}
