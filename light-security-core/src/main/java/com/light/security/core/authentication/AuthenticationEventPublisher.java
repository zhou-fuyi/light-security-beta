package com.light.security.core.authentication;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;

/**
 * @InterfaceName AuthenticationEventPublisher
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public interface AuthenticationEventPublisher {

    void publishAuthenticationSuccess(Authentication authentication);

    void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication);
}
