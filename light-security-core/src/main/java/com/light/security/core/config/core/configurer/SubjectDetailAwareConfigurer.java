package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.config.core.ProviderManagerBuilder;
import com.light.security.core.config.core.SecurityConfigurerAdapter;

/**
 * @ClassName SubjectDetailAwareConfigurer
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public abstract class SubjectDetailAwareConfigurer<B extends ProviderManagerBuilder<B>, U extends SubjectDetailService> extends SecurityConfigurerAdapter<AuthenticationManager, B> {

    public abstract U getSubjectDetailService();
}
