package com.light.security.core.access;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;

/**
 * @InterfaceName AuthenticationProvider
 * @Description 认证接口
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public interface AuthenticationProvider {

    Authentication authenticate(Authentication authentication) throws AuthenticationException;

    boolean supports(Class<?> authentication);

}
