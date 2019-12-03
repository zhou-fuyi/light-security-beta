package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.context.repository.InternalSecurityContextRepository;
import com.light.security.core.authentication.context.repository.SecurityContextRepository;
import com.light.security.core.config.core.builder.FilterChainBuilder;
import com.light.security.core.filter.SecurityContextPretreatmentFilter;
import org.springframework.util.Assert;

/**
 * @ClassName SecurityContextConfigurer
 * @Description {@link com.light.security.core.filter.SecurityContextPretreatmentFilter}过滤器配置器
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class SecurityContextConfigurer<B extends FilterChainBuilder<B>> extends AbstractHttpConfigurer<SecurityContextConfigurer<B>, B> {

    private SecurityContextHolder securityContextHolder;

    public SecurityContextConfigurer(SecurityContextHolder securityContextHolder){
        Assert.notNull(securityContextHolder, "构造器不接受空值参数 --> securityContextHolder is null");
        this.securityContextHolder = securityContextHolder;
    }

    /**
     * 将{@link SecurityContextRepository}对象放入共享对象中, 以便后续使用
     * @param securityContextRepository
     * @return
     */
    public SecurityContextConfigurer<B> securityContextRepository(SecurityContextRepository securityContextRepository){
        getBuilder().setSharedObject(SecurityContextRepository.class, securityContextRepository);
        return this;
    }

    @Override
    public void configure(B builder) throws Exception {
        SecurityContextRepository securityContextRepository = builder.getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository == null){
            securityContextRepository = new InternalSecurityContextRepository();
        }
        SecurityContextPretreatmentFilter securityContextPretreatmentFilter = new SecurityContextPretreatmentFilter(securityContextRepository, securityContextHolder);
        securityContextPretreatmentFilter = postProcess(securityContextPretreatmentFilter);
        builder.addFilter(securityContextPretreatmentFilter);
    }
}
