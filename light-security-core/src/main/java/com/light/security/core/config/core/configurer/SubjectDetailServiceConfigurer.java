package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.config.core.ProviderManagerBuilder;

/**
 * @ClassName SubjectDetailServiceConfigurer
 * @Description Allows configuring a {@link SubjectDetailService} within a {@link com.light.security.core.config.core.builder.AuthenticationManagerBuilder}.
 * 允许在{@link com.light.security.core.config.core.builder.AuthenticationManagerBuilder}中配置{@link SubjectDetailService}。
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class SubjectDetailServiceConfigurer<B extends ProviderManagerBuilder<B>, C extends SubjectDetailServiceConfigurer<B, C, U>, U extends SubjectDetailService>
        extends AbstractDaoAuthenticationConfigurer<B, C, U>{

    public SubjectDetailServiceConfigurer(U subjectDetailService) {
        super(subjectDetailService);
    }

    @Override
    public void configure(B builder) throws Exception {
        initSubjectDetailService();
        super.configure(builder);
    }

    /**
     * Allows subclasses to initialize the {@link SubjectDetailService}. For example, it
     * might add users, initialize schema, etc.
     *
     * 允许子类初始化{@link SubjectDetailService}。 例如，它可能会添加用户，初始化架构等。
     * @throws Exception
     */
    protected void initSubjectDetailService() throws Exception{}
}
