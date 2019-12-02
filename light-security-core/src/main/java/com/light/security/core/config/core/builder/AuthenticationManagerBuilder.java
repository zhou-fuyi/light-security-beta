package com.light.security.core.config.core.builder;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.AuthenticationEventPublisher;
import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.ProviderManager;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.config.core.AbstractConfiguredSecurityBuilder;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.ProviderManagerBuilder;
import com.light.security.core.config.core.configurer.DaoAuthenticationConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AuthenticationManagerBuilder
 * @Description AuthenticationManagerBuilder 目的是构建AuthenticationManager
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class AuthenticationManagerBuilder extends AbstractConfiguredSecurityBuilder<AuthenticationManager, AuthenticationManagerBuilder>
        implements ProviderManagerBuilder<AuthenticationManagerBuilder> {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private AuthenticationManager parentAuthenticationManager;
    private List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
    private SubjectDetailService defaultSubjectDetailService;
    private Boolean eraseCredentials;//是否擦除凭证
    private AuthenticationEventPublisher eventPublisher;

    public AuthenticationManagerBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
        //这里指定了该实例中 allowConfigurersOfSameType 参数为true, 表示可以存储相同类型的configurer
        super(objectPostProcessor, true);
    }

    public AuthenticationManagerBuilder parentAuthenticationManager(AuthenticationManager authenticationManager){
        if (authenticationManager instanceof ProviderManager){
            eraseCredentials(((ProviderManager) authenticationManager).isEraseCredentialsAfterAuthentication());
        }
        this.parentAuthenticationManager = authenticationManager;
        return this;
    }

    public AuthenticationManagerBuilder authenticationEventPublisher(AuthenticationEventPublisher eventPublisher){
        Assert.notNull(eventPublisher, "AuthenticationEventPublisher 不能为 null");
        this.eventPublisher = eventPublisher;
        return this;
    }

    public AuthenticationManagerBuilder eraseCredentials(boolean eraseCredentials) {
        this.eraseCredentials = eraseCredentials;
        return this;
    }

    // TODO: 2019-12-02 顺序往下， 到这里了 
    /**
     * 
     * @param subjectDetailService
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T extends SubjectDetailService> DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T> subjectDetailService(T subjectDetailService) throws Exception{
        this.defaultSubjectDetailService = subjectDetailService;
        return apply(new DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T>(subjectDetailService));
    }

    @Override
    protected AuthenticationManager performBuild() throws Exception {
        return null;
    }

    @Override
    public AuthenticationManagerBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
        return null;
    }

}
