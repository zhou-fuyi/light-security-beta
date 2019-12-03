package com.light.security.core.authentication;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.token.AbstractAuthenticationToken;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AccountStatusException;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.InternalAuthenticationServiceException;
import com.light.security.core.exception.ProviderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName ProviderManager
 * @Description {@link AuthenticationManager}的默认实现
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class ProviderManager implements AuthenticationManager, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ProviderManager.class);
    private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();
    private List<AuthenticationProvider> authenticationProviders = Collections.EMPTY_LIST;
    private AuthenticationManager parent;

    /**
     * 是否在认证之后擦除凭证
     */
    private boolean eraseCredentialsAfterAuthentication = true;

    public ProviderManager(List<AuthenticationProvider> authenticationProviders){
        this(authenticationProviders, null);
    }

    public ProviderManager(List<AuthenticationProvider> authenticationProviders, AuthenticationManager parent){
        Assert.notNull(authenticationProviders, "providers list cannot be null");
        this.authenticationProviders = authenticationProviders;
        this.parent = parent;
        checkState();
    }

    private void checkState() {
        if (parent == null && authenticationProviders.isEmpty()){
            throw new IllegalArgumentException("AuthenticationManager 或 List<AuthenticationProvider> 必须有一个可用");
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        Authentication result = null;
        boolean debug = logger.isDebugEnabled();

        for (AuthenticationProvider provider : getAuthenticationProviders()){
            if (!provider.supports(toTest)){
                continue;
            }
            if (debug){
                logger.debug("Authentication attempt using {}", provider.getClass().getName());
            }

            try {
                result = provider.authenticate(authentication);
                if (result != null){
                    copyDetail(authentication, result);
                    break;
                }
            }catch (AccountStatusException ex){
                prepareException(ex, authentication);
                throw ex;
            }catch (InternalAuthenticationServiceException ex){
                prepareException(ex, authentication);
            }catch (AuthenticationException ex){
                lastException = ex;
            }
        }

        //做最后的挣扎
        if (result == null && parent != null){
            try {
                result = parent.authenticate(authentication);
            }catch (ProviderNotFoundException ex){
                /**
                 * ignore as we will throw below if no other exception occurred prior to
                 * calling parent and the parent
                 * may throw ProviderNotFound even though a provider in the child already
                 * handled the request
                 */
                throw ex;
            }catch (AuthenticationException ex){
                lastException = ex;
            }
        }

        if (result != null){
            if (eraseCredentialsAfterAuthentication && (result instanceof CredentialsContainer)){
                ((CredentialsContainer) result).eraseCredentials();
            }
            eventPublisher.publishAuthenticationSuccess(result);
            return result;
        }

        if (lastException == null){
            lastException = new ProviderNotFoundException(500, "找不到适应当前 Authentication 的 AuthenticationProvider");
        }
        prepareException(lastException, authentication);
        throw lastException;
    }

    private void prepareException(AuthenticationException ex, Authentication authentication) {
        eventPublisher.publishAuthenticationFailure(ex, authentication);
    }

    private void copyDetail(Authentication source, Authentication dest) {
        if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() != null)){
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;
            token.setDetails(source.getDetails());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkState();
    }

    public void setEraseCredentialsAfterAuthentication(boolean eraseSecretData) {
        this.eraseCredentialsAfterAuthentication = eraseSecretData;
    }

    public List<AuthenticationProvider> getAuthenticationProviders() {
        return authenticationProviders;
    }

    public boolean isEraseCredentialsAfterAuthentication() {
        return eraseCredentialsAfterAuthentication;
    }

    public void setAuthenticationEventPublisher(AuthenticationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 空实现
     */
    private static final class NullEventPublisher implements AuthenticationEventPublisher{

        @Override
        public void publishAuthenticationSuccess(Authentication authentication) {

        }

        @Override
        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {

        }
    }
}
