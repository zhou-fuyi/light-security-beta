package com.light.security.core.exception;

/**
 * @ClassName InternalAuthenticationServiceException
 * @Description 仿照SpringSecurity完成
 * Thrown if an authentication request could not be processed due to a system problem that occurred internally.
 * It differs from {@link AuthenticationServiceException} in that it  would not be thrown if an external system has an internal error or failure.
 * This  ensures that we can handle errors that are within our control distinctly from errors of other systems.
 * The advantage to this distinction is that the untrusted external system should not be able to fill up logs and cause excessive IO.
 * However, an internal system should report errors
 *
 * 翻译：
 * 如果由于内部发生系统问题而无法处理身份验证请求，则抛出该异常。
 * 它与{@link AuthenticationServiceException}的不同之处在于，
 * 如果外部系统发生内部错误或故障，则不会抛出该异常。 这确保了我们可以处理与其他系统错误完全不同的控制错误。
 * 这种区别的优点在于，不受信任的外部系统不应填充日志并导致过多的IO。 但是，内部系统应报告错误。
 *
 * 自行理解：
 * 由于出现内部的 认证流程或是逻辑上的问题 而拒绝身份验证请求, 则抛出此异常.
 * 如在进行SubjectService.loadSubjectBySubjectName时, 返回了null值
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class InternalAuthenticationServiceException extends AuthenticationServiceException {

    public InternalAuthenticationServiceException(Integer code, String msg) {
        super(code, msg);
    }
}
