package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.dao.DaoAuthenticationProvider;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.ProviderManagerBuilder;
import com.light.security.core.util.crypto.password.PasswordEncoder;

/**
 * @ClassName AbstractDaoAuthenticationConfigurer
 * @Description Allows configuring a {@link DaoAuthenticationProvider}
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public abstract class AbstractDaoAuthenticationConfigurer<B extends ProviderManagerBuilder<B>, C extends AbstractDaoAuthenticationConfigurer<B, C, U>,
        U extends SubjectDetailService> extends SubjectDetailAwareConfigurer<B, U> {

    private DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    private final U subjectDetailService;

    protected AbstractDaoAuthenticationConfigurer(U subjectDetailService){
        this.subjectDetailService = subjectDetailService;
        provider.setSubjectDetailService(subjectDetailService);
    }

    /**
     * Adds an {@link ObjectPostProcessor} for this class.
     * @param objectPostProcess
     * @return
     */
    public C withObjectPostProcess(ObjectPostProcessor<?> objectPostProcess){
        addObjectPostProcessor(objectPostProcess);
        return (C) this;
    }

    public C passEncoder(PasswordEncoder passwordEncoder){
        provider.setPasswordEncoder(passwordEncoder);
        return (C) this;
    }

    @Override
    public void configure(B builder) throws Exception {
        //给Builder添加AuthenticationProvider
        provider = postProcess(provider);
        builder.authenticationProvider(provider);
    }

    @Override
    public U getSubjectDetailService() {
        return subjectDetailService;
    }
}
