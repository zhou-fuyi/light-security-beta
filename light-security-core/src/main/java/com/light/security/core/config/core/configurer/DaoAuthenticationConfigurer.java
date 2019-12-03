package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.config.core.ProviderManagerBuilder;

/**
 * @ClassName DaoAuthenticationConfigurer
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class DaoAuthenticationConfigurer<B extends ProviderManagerBuilder<B>, U extends SubjectDetailService> extends AbstractDaoAuthenticationConfigurer<B, DaoAuthenticationConfigurer<B, U>, U> {

    public DaoAuthenticationConfigurer(U subjectDetailService) {
        super(subjectDetailService);
    }

    @Override
    public U getSubjectDetailService() {
        return null;
    }
}
