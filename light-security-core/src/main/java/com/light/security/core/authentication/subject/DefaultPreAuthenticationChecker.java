package com.light.security.core.authentication.subject;

import com.light.security.core.exception.AccountExpiredException;
import com.light.security.core.exception.DisabledException;
import com.light.security.core.exception.LockedException;


/**
 * @ClassName DefaultPreAuthenticationChecker
 * @Description 认证前账户主体数据检查器, 检查账户的状态
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class DefaultPreAuthenticationChecker extends AbstractSubjectDetailChecker{

    @Override
    public void check(SubjectDetail detail) {
        if (!detail.isAccountNonLocked()){
            logger.debug("账户已被锁定");
            throw new LockedException(401, "账户已被锁定");
        }
        if (!detail.isEnabled()) {
            logger.debug("账户已被禁用");

            throw new DisabledException(401, "账户已被禁用");
        }

        if (!detail.isAccountNonExpired()) {
            logger.debug("账户已过期");

            throw new AccountExpiredException(401, "账户已过期");
        }
    }
}
