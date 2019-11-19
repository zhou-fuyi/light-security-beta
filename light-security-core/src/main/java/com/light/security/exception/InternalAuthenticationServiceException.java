package com.light.security.exception;

/**
 * @ClassName InternalAuthenticationServiceException
 * @Description 由于出现内部认证流程或是逻辑上的问题而拒绝身份验证请求, 则抛出此异常.
 * 如在进行SubjectService.loadSubjectBySubjectName时, 返回了null值
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class InternalAuthenticationServiceException extends AuthenticationServiceException {

    public InternalAuthenticationServiceException(Integer code, String msg) {
        super(code, msg);
    }
}
