package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.dao.DaoAuthenticationProvider;
import com.light.security.core.config.core.ProviderManagerBuilder;

/**
 * @ClassName AbstractDaoAuthenticationConfigurer
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public abstract class AbstractDaoAuthenticationConfigurer<B extends ProviderManagerBuilder<B>, C extends AbstractDaoAuthenticationConfigurer<B, C, U>,
        U extends SubjectDetailService> extends SubjectDetailAwareConfigurer<B, U> {

    private DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    private final U subjectDetailService;

    protected AbstractDaoAuthenticationConfigurer(U subjectDetailService){
        this.subjectDetailService = subjectDetailService;
    }
}
