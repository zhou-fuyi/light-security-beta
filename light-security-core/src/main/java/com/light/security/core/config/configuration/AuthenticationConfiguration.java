package com.light.security.core.config.configuration;

import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.builder.AuthenticationManagerBuilder;
import com.light.security.core.config.core.configurer.EnableGlobalAuthenticationAutowiredConfigurer;
import com.light.security.core.config.core.configurer.GlobalAuthenticationConfigurerAdapter;
import com.light.security.core.config.core.configurer.InitializeAuthenticationProviderBeanManagerConfigurer;
import com.light.security.core.config.core.configurer.InitializeSubjectDetailBeanManagerConfigurer;
import com.light.security.core.exception.AuthenticationException;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName AuthenticationConfiguration
 * @Description 仿照SpringSecurity完成
 * Exports the authentication {@link Configuration}
 *
 *
 * 翻译：
 * 主要是进行身份验证的配置
 * @Author ZhouJian
 * @Date 2019-12-03
 */
@Configuration
@Import(ObjectPostProcessorConfiguration.class)
public class AuthenticationConfiguration {

    /**
     * 标志位, 记录 {@link #authenticationManager} 是否正在构建中
     */
    private AtomicBoolean buildingAuthenticationManager = new AtomicBoolean();

    private ApplicationContext applicationContext;

    private AuthenticationManager authenticationManager;

    /**
     * 标识 {@link #authenticationManager}是否已经构建完成
     */
    private boolean authenticationManagerInitialized;

    private List<GlobalAuthenticationConfigurerAdapter> globalAuthenticationConfigurers = Collections.EMPTY_LIST;

    private ObjectPostProcessor<Object> objectPostProcessor;

    /**
     * 创建{@link AuthenticationManagerBuilder}对象
     * @param objectPostProcessor
     * @return
     */
    @Bean
    public AuthenticationManagerBuilder authenticationManagerBuilder(ObjectPostProcessor<Object> objectPostProcessor){
        return new AuthenticationManagerBuilder(objectPostProcessor);
    }

    @Bean
    public static GlobalAuthenticationConfigurerAdapter enableGlobalAuthenticationConfigurerAdapter(ApplicationContext context){
        return new EnableGlobalAuthenticationAutowiredConfigurer(context);
    }

    @Bean
    public static InitializeSubjectDetailBeanManagerConfigurer initializeSubjectDetailBeanManagerConfigurer(ApplicationContext context){
        return new InitializeSubjectDetailBeanManagerConfigurer(context);
    }

    @Bean
    public static InitializeAuthenticationProviderBeanManagerConfigurer initializeAuthenticationProviderBeanManagerConfigurer(ApplicationContext context){
        return new InitializeAuthenticationProviderBeanManagerConfigurer(context);
    }

    /**
     * 创建{@link #authenticationManager}
     *
     * AuthenticationManager 对象创建流程参考: https://blog.csdn.net/andy_zhang2007/article/details/89885100
     * @return
     * @throws Exception
     */
    public AuthenticationManager getAuthenticationManager() throws Exception{
        if (this.authenticationManagerInitialized){
            return this.authenticationManager;
        }
        AuthenticationManagerBuilder authenticationManagerBuilder = authenticationManagerBuilder(this.objectPostProcessor);
        if (this.buildingAuthenticationManager.getAndSet(true)){
            return new AuthenticationManagerDelegator(authenticationManagerBuilder);
        }

        for (GlobalAuthenticationConfigurerAdapter configurerAdapter : globalAuthenticationConfigurers){
            authenticationManagerBuilder.apply(configurerAdapter);
        }

        authenticationManager = authenticationManagerBuilder.build();
        if (authenticationManager == null){
            authenticationManager = getAuthenticationManagerBean();
        }
        this.authenticationManagerInitialized = true;
        return authenticationManager;
    }


    @Autowired(required = false)
    public void setGlobalAuthenticationConfigurers(List<GlobalAuthenticationConfigurerAdapter> configurers){
        Collections.sort(configurers, AnnotationAwareOrderComparator.INSTANCE);
        this.globalAuthenticationConfigurers = configurers;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;
    }

    // TODO: 2019-12-03 理解逻辑，执行顺序
    private <T> T lazyBean(Class<T> interfaceName) {
        LazyInitTargetSource lazyTargetSource = new LazyInitTargetSource();
        String[] beanNamesForType = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, interfaceName);
        if (beanNamesForType.length == 0) {
            return null;
        }
        Assert.isTrue(beanNamesForType.length == 1, "Expecting to only find a single bean for type " + interfaceName
                + ", but found " + Arrays.asList(beanNamesForType));
        lazyTargetSource.setTargetBeanName(beanNamesForType[0]);
        lazyTargetSource.setBeanFactory(applicationContext);
        ProxyFactoryBean proxyFactory = new ProxyFactoryBean();
        proxyFactory = objectPostProcessor.postProcess(proxyFactory);
        proxyFactory.setTargetSource(lazyTargetSource);
        return (T) proxyFactory.getObject();
    }

    private AuthenticationManager getAuthenticationManagerBean() {
        return lazyBean(AuthenticationManager.class);
    }


    /**
     * Prevents infinite recursion in the event that initializing the
     * AuthenticationManager.
     *
     *
     * 防止在初始化AuthenticationManager时进行无限递归。
     */
    static final class AuthenticationManagerDelegator implements AuthenticationManager {
        private AuthenticationManagerBuilder delegateBuilder;
        private AuthenticationManager delegate;
        private final Object delegateMonitor = new Object();

        AuthenticationManagerDelegator(AuthenticationManagerBuilder delegateBuilder) {
            Assert.notNull(delegateBuilder, "delegateBuilder cannot be null");
            this.delegateBuilder = delegateBuilder;
        }

        @Override
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            if (this.delegate != null) {
                return this.delegate.authenticate(authentication);
            }

            synchronized (this.delegateMonitor) {
                if (this.delegate == null) {
                    this.delegate = this.delegateBuilder.getTarget();
                    this.delegateBuilder = null;
                }
            }

            return this.delegate.authenticate(authentication);
        }

        @Override
        public String toString() {
            return "AuthenticationManagerDelegator [delegate=" + this.delegate + "]";
        }
    }

}
