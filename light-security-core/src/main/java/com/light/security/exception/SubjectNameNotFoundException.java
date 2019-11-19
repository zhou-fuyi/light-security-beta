package com.light.security.exception;

/**
 * @ClassName SubjectNameNotFoundException
 * @Description 由于认证账户主体的账户名称错误而拒绝身份验证请求, 则抛出此异常
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class SubjectNameNotFoundException extends AuthenticationException {

    public SubjectNameNotFoundException(Integer code, String msg) {
        super(code, msg);
    }
}
