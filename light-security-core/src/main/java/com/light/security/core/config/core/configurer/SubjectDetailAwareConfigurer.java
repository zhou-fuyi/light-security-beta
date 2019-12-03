package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.config.core.ProviderManagerBuilder;
import com.light.security.core.config.core.SecurityConfigurerAdapter;

/**
 * @ClassName SubjectDetailAwareConfigurer
 * @Description 允许访问{@link UserDetailsService}并将其用作{@link com.light.security.core.config.core.builder.AuthenticationManagerBuilder}的默认值的基类。
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public abstract class SubjectDetailAwareConfigurer<B extends ProviderManagerBuilder<B>, U extends SubjectDetailService> extends SecurityConfigurerAdapter<AuthenticationManager, B> {

    /**
     * 获取{@link SubjectDetailService}；如果不可用，则返回null
     * @return
     */
    public abstract U getSubjectDetailService();
}
