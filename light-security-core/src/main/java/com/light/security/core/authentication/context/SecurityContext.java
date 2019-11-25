package com.light.security.core.authentication.context;

import com.light.security.core.authentication.token.Authentication;

/**
 * @InterfaceName SecurityContext
 * @Description 用于包装Authentication, 与当前线程内共享
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public interface SecurityContext {

    Authentication getAuthentication();


    void setAuthentication(Authentication authentication);

}
