package com.light.security.core.config.core.configurer;

import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.SecurityConfigurerAdapter;
import com.light.security.core.config.core.builder.FilterChainBuilder;
import com.light.security.core.filter.chain.DefaultSecurityFilterChain;

/**
 * @ClassName AbstractHttpConfigurer
 * @Description Adds a convenient base class for {@link com.light.security.core.config.core.SecurityConfigurer} instances that operate on {@link FilterChainBuilder}.
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public abstract class AbstractHttpConfigurer<T extends AbstractHttpConfigurer<T, B>, B extends FilterChainBuilder<B>> extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {

    /**
     * Disables the {@link AbstractHttpConfigurer} by removing it. After doing so a fresh version of the configuration can be applied.
     *
     * 通过删除{@link AbstractHttpConfigurer}禁用它。 这样做之后，可以应用新版本的配置。
     * @return
     */
    public B disable(){
        getBuilder().removeConfigurer(getClass());
        return getBuilder();
    }

    public T withObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor){
        addObjectPostProcessor(objectPostProcessor);
        return (T) this;
    }
}
