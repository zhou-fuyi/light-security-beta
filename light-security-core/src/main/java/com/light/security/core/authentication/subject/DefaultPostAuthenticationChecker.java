package com.light.security.core.authentication.subject;

import com.light.security.core.exception.CredentialsExpiredException;

/**
 * @ClassName DefaultPostAuthenticationChecker
 * @Description 账户数据后置检查器
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class DefaultPostAuthenticationChecker extends AbstractSubjectDetailChecker {
    @Override
    public void check(SubjectDetail detail) {
        if (!detail.isCredentialsNonExpired()){
            logger.debug("用户凭证已过期");

            throw new CredentialsExpiredException(401, "用户凭证已过期");
        }
    }
}
