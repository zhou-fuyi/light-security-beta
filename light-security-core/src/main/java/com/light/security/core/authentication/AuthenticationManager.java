package com.light.security.core.authentication;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;

/**
 * @InterfaceName AuthenticationManager
 * @Description 认证管理器
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public interface AuthenticationManager {

    /**
     * 主体认证接口
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    Authentication authenticate(Authentication authentication) throws AuthenticationException;

}
