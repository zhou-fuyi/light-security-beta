package com.light.security.core.config.core.builder;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.AuthenticationEventPublisher;
import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.ProviderManager;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.config.core.AbstractConfiguredSecurityBuilder;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.ProviderManagerBuilder;
import com.light.security.core.config.core.configurer.DaoAuthenticationConfigurer;
import com.light.security.core.config.core.configurer.SubjectDetailAwareConfigurer;
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


    /**
     * Add authentication based upon the custom {@link SubjectDetailService} that is passed
     * in. It then returns a {@link DaoAuthenticationConfigurer} to allow customization of
     * the authentication.
     *
     * <p>
     * This method also ensure that the {@link SubjectDetailService} is available for the
     * {@link #getDefaultSubjectDetailService()} method. Note that additional
     * {@link SubjectDetailService}'s may override this {@link SubjectDetailService} as the
     * default.
     * </p>
     *
     *
     * 翻译:
     * 根据传入的自定义{@link SubjectDetailService}添加身份验证。然后返回一个{@link DaoAuthenticationConfigurer}以允许自定义身份验证。
     * <p>
     * 此方法还确保{@link SubjectDetailService}可用于{@link #getDefaultSubjectDetailService（）}方法。
     * 请注意，其他{@link SubjectDetailService}可能会覆盖此{@link SubjectDetailService}作为默认设置。
     * </p>
     * @param subjectDetailService
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T extends SubjectDetailService> DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T> subjectDetailService(T subjectDetailService) throws Exception{
        this.defaultSubjectDetailService = subjectDetailService;
        return apply(new DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T>(subjectDetailService));
    }

    public SubjectDetailService getDefaultSubjectDetailService() {
        return defaultSubjectDetailService;
    }


    @Override
    public AuthenticationManagerBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProviders.add(authenticationProvider);
        return this;
    }


    /**
     * 旨在创建ProviderManager
     *
     * @return
     * @throws Exception
     */
    @Override
    protected AuthenticationManager performBuild() throws Exception {
        if (!isConfigured()){
            if(logger.isDebugEnabled()){
                logger.debug("No authenticationProviders and no parentAuthenticationManager defined. Returning null.");
            }
            return null;
        }
        ProviderManager providerManager = new ProviderManager(authenticationProviders, parentAuthenticationManager);
        if(eraseCredentials != null){
            providerManager.setEraseCredentialsAfterAuthentication(eraseCredentials);
        }
        if (eventPublisher != null){
            providerManager.setAuthenticationEventPublisher(eventPublisher);
        }
        providerManager = postProcess(providerManager);
        return providerManager;
    }

    /**
     * 确保认证功能的可用, 确保{@link #parentAuthenticationManager}与{@link #authenticationProviders}至少有一个是可用的
     * {@link #parentAuthenticationManager}可用要求为不为null
     * {@link #authenticationProviders}可用要求为至少存在一个元素
     * @return
     */
    public boolean isConfigured(){
        return !authenticationProviders.isEmpty() || parentAuthenticationManager != null;
    }

    /**
     * 添加配置器
     * 限定C上界为 <code> SubjectDetailAwareConfigurer<AuthenticationManagerBuilder, ? extends SubjectDetailService> </code>
     * @param configurer
     * @param <C>
     * @return
     * @throws Exception
     */
    private <C extends SubjectDetailAwareConfigurer<AuthenticationManagerBuilder, ? extends SubjectDetailService>> C apply(C configurer) throws Exception {
        this.defaultSubjectDetailService = configurer.getSubjectDetailService();
        return (C) super.apply(configurer);
    }
}
