package com.light.security.core.exception;

/**
 * @ClassName AuthenticationServiceException
 * @Description 仿照 SpringSecurity 完成
 * Thrown if an authentication request could not be processed due to a system problem.
 * This might be thrown if a backend authentication repository is unavailable, for example.
 *
 * 翻译：
 * 如果由于系统问题而无法处理身份验证请求，则抛出该异常。
 * 例如，如果后端身份验证存储库不可用，则可能会抛出此错误。
 *
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class AuthenticationServiceException extends AuthenticationException {

    public AuthenticationServiceException(Integer code, String msg) {
        super(code, msg);
    }
}
