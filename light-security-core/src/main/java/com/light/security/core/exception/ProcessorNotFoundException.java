package com.light.security.core.exception;

/**
 * @ClassName ProviderNotFoundException
 * @Description 如果找不到支持 {@link com.light.security.core.constant.AuthTypeEnum}的
 * {@link com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessor}就抛出此异常
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public class ProcessorNotFoundException extends AuthenticationException {
    public ProcessorNotFoundException(Integer code, String msg) {
        super(code, msg);
    }
}
