package com.light.security.core.authentication;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;
import org.springframework.beans.factory.InitializingBean;

/**
 * @ClassName ProviderManager
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class ProviderManager implements AuthenticationManager, InitializingBean {

    /**
     * 是否在认证之后擦除凭证
     */
    private boolean eraseCredentialsAfterAuthentication = true;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public void setEraseCredentialsAfterAuthentication(boolean eraseSecretData) {
        this.eraseCredentialsAfterAuthentication = eraseSecretData;
    }

    public boolean isEraseCredentialsAfterAuthentication() {
        return eraseCredentialsAfterAuthentication;
    }
}
