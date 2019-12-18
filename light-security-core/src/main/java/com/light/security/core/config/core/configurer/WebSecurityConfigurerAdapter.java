package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.*;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.context.repository.SecurityContextRepository;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.config.configuration.AuthenticationConfiguration;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.WebSecurityConfigurer;
import com.light.security.core.config.core.builder.AuthenticationManagerBuilder;
import com.light.security.core.config.core.builder.ChainProxyBuilder;
import com.light.security.core.config.core.builder.HttpSecurityBuilder;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.SubjectNameNotFoundException;
import com.light.security.core.filter.FilterSecurityInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.accept.ContentNegotiationStrategy;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @ClassName WebSecurityConfigurerAdapter
 * @Description 仿照SpringSecurity完成
 * Provides a convenient base class for creating a {@link WebSecurityConfigurer}
 * instance. The implementation allows customization by overriding methods.
 *
 * <p>
 * Will automatically apply the result of looking up
 * {@link AbstractHttpConfigurer} from {@link SpringFactoriesLoader} to allow
 * developers to extend the defaults.
 * To do this, you must create a class that extends AbstractHttpConfigurer and then create a file in the classpath at "META-INF/spring.factories" that looks something like:
 * </p>
 * <pre>
 * org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer = sample.MyClassThatExtendsAbstractHttpConfigurer
 * </pre>
 * If you have multiple classes that should be added you can use "," to separate the values. For example:
 *
 * <pre>
 * org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer = sample.MyClassThatExtendsAbstractHttpConfigurer, sample.OtherThatExtendsAbstractHttpConfigurer
 * </pre>
 *
 *
 * 翻译:
 * 提供用于创建{@link WebSecurityConfigurer}实例的便捷基类。 该实现允许通过覆盖方法进行自定义。
 * 将自动应用从{@link SpringFactoriesLoader}中查找{@link AbstractHttpConfigurer}的结果，以允许
 * 开发人员可以扩展默认值。
 * 为此，您必须创建一个扩展AbstractHttpConfigurer的类，然后在类路径中的“ META-INF / spring.factories”处创建一个文件，该文件如下所示：
 * org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer = sample.MyClassThatExtendsAbstractHttpConfigurer
 *
 * @Author ZhouJian
 * @Date 2019-11-29
 */
@Order(100)
public abstract class WebSecurityConfigurerAdapter implements WebSecurityConfigurer<ChainProxyBuilder> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext context;

    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
        @Override
        public <O> O postProcess(O object) {
            throw new IllegalStateException(ObjectPostProcessor.class.getName() + " is a required bean. Ensure you have used @EnableWebSecurity and @Configuration");
        }
    };


    private AuthenticationConfiguration authenticationConfiguration;
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private AuthenticationManagerBuilder localAuthenticationManagerBuilder;

    private boolean disabledLocalAuthenticationManagerBuilder;
    private boolean authenticationManagerInitialized;
    private AuthenticationManager authenticationManager;
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private HttpSecurityBuilder httpSecurityBuilder;
    /**
     * 是否禁用默认配置: true表示禁用默认配置, false表示启用默认配置
     */
    private boolean disableDefaults;

    @Autowired
    private SecurityContextHolder securityContextHolder;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext){
        this.context = applicationContext;
    }

    @Autowired(required = false)
    public void setAuthenticationTrustResolver(AuthenticationTrustResolver authenticationTrustResolver){
        this.trustResolver = authenticationTrustResolver;
    }

    /**
     * 注入{@link #objectPostProcessor}实例
     * 同时初始化{@link #authenticationManagerBuilder}和{@link #localAuthenticationManagerBuilder}
     * @param objectPostProcessor
     */
    @Autowired
    public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor){
        this.objectPostProcessor = objectPostProcessor;
        authenticationManagerBuilder = new AuthenticationManagerBuilder(objectPostProcessor);
        localAuthenticationManagerBuilder = new AuthenticationManagerBuilder(objectPostProcessor){
            /**
             * 这里保障, 如果修改了<code> localAuthenticationManagerBuilder </code> 的 eraseCredentials 属性
             * 也会同时修改<code> authenticationManagerBuilder </code>的 eraseCredentials, 保持值相同
             * @param eraseCredentials
             * @return
             */
            @Override
            public AuthenticationManagerBuilder eraseCredentials(boolean eraseCredentials) {
                authenticationManagerBuilder.eraseCredentials(eraseCredentials);
                return super.eraseCredentials(eraseCredentials);
            }
        };
    }

    @Autowired
    public void setAuthenticationConfiguration(AuthenticationConfiguration authenticationConfiguration){
        this.authenticationConfiguration = authenticationConfiguration;
    }

    protected WebSecurityConfigurerAdapter(){
        this(false);
    }

    /**
     * Creates an instance which allows specifying if the default configuration should be
     * enabled. Disabling the default configuration should be considered more advanced
     * usage as it requires more understanding of how the framework is implemented.
     *
     *
     * 创建一个实例，该实例允许指定是否应启用默认配置。 禁用默认配置应该被认为是更高级的用法，因为它需要对框架的实现方式有更多的了解。
     *
     * @param disableDefaults true if the default configuration should be disabled, else
     * false
     */
    protected WebSecurityConfigurerAdapter(boolean disableDefaults){
        this.disableDefaults = disableDefaults;
    }


    /**
     * 创建{@link HttpSecurityBuilder}实例
     * @return
     * @throws Exception
     */
    protected final HttpSecurityBuilder getHttpSecurityBuilder() throws Exception {
        if (httpSecurityBuilder != null){
            return httpSecurityBuilder;
        }

        DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor.postProcess(new DefaultAuthenticationEventPublisher());
        //设置事件发布器
        localAuthenticationManagerBuilder.authenticationEventPublisher(eventPublisher);

        //获取AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManager();

        /**
         * 将获取到的AuthenticationManager注入到 {@link #authenticationManagerBuilder}的parentAuthenticationManager中
         */
        authenticationManagerBuilder.parentAuthenticationManager(authenticationManager);

        Map<Class<? extends Object>, Object> sharedObjects = createSharedObjects();

        httpSecurityBuilder = new HttpSecurityBuilder(objectPostProcessor, authenticationManagerBuilder, sharedObjects);
        /**
         * disableDefaults: 禁用默认配置
         * 为true表示禁用默认配置,
         * false表示使用默认配置
         */
        if (!disableDefaults){
            httpSecurityBuilder.cors().and()
                    .anonymous(securityContextHolder).and()
                    .authorize().and()
                    .securityContext(securityContextHolder).securityContextRepository(securityContextRepository).and()
                    .formLogin(securityContextHolder).and()
                    .exceptionTranslation(securityContextHolder);

            ClassLoader classLoader = this.context.getClassLoader();
            List<AbstractHttpConfigurer> defaultHttpConfigurers = SpringFactoriesLoader.loadFactories(AbstractHttpConfigurer.class, classLoader);
            for (AbstractHttpConfigurer httpConfigurer : defaultHttpConfigurers){
                httpSecurityBuilder.apply(httpConfigurer);
            }
        }

        configure(httpSecurityBuilder);
        return httpSecurityBuilder;
    }

    /**
     * 创建共享对象
     * @return
     */
    private Map<Class<? extends Object>, Object> createSharedObjects(){
        Map<Class<? extends Object>, Object> sharedObjects = new HashMap<>();
        sharedObjects.putAll(localAuthenticationManagerBuilder.getSharedObjects());
        sharedObjects.put(SubjectDetailService.class, subjectDetailService());
        sharedObjects.put(ApplicationContext.class, context);
        sharedObjects.put(AuthenticationTrustResolver.class, trustResolver);
        sharedObjects.put(SecurityContextHolder.class, securityContextHolder);
        return sharedObjects;
    }

    /**
     * Override this method to expose the {@link AuthenticationManager} from
     * {@link #configure(AuthenticationManagerBuilder)} to be exposed as a Bean. For
     * example:
     *
     * <pre>
     * &#064;Bean(name name="myAuthenticationManager")
     * &#064;Override
     * public AuthenticationManager authenticationManagerBean() throws Exception {
     *     return super.authenticationManagerBean();
     * }
     * </pre>
     *
     * 重写此方法以将{@link #configure（AuthenticationManagerBuilder）}中的{@link AuthenticationManager}公开为Bean。
     * 例如(应该是这个意思吧):
     * <pre>
     * @Bean(name="myAuthenticationManager")
     * @Override
     * public AuthenticationManager authenticationManagerBean() throws Exception {
     *     return super.authenticationManagerBean();
     * }
     * </pre>
     * @return the {@link AuthenticationManager}
     * @throws Exception
     */
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return new AuthenticationManagerDelegator(authenticationManagerBuilder, context);
    }

    /**
     * 获取要使用的{@link AuthenticationManager}。
     * 默认策略是如果 {@link #configure（AuthenticationManagerBuilder）}方法被覆盖以使用传入的{@link AuthenticationManagerBuilder}。
     * 否则，按类型自动连接{@link AuthenticationManager}。
     * @return
     * @throws Exception
     */
    protected AuthenticationManager authenticationManager() throws Exception{
        if (!authenticationManagerInitialized){
            configure(localAuthenticationManagerBuilder);
            //关键属性：disabledLocalAuthenticationManagerBuilder
            if (disabledLocalAuthenticationManagerBuilder){
                authenticationManager = authenticationConfiguration.getAuthenticationManager();
            }else {
                authenticationManager = localAuthenticationManagerBuilder.build();
            }
            authenticationManagerInitialized = true;
        }
        return authenticationManager;
    }

    /**
     * Override this method to expose a {@link SubjectDetailService} created from
     * {@link #configure(AuthenticationManagerBuilder)} as a bean. In general only the
     * following override should be done of this method:
     *
     * <pre>
     * &#064;Bean(name = &quot;myUserDetailsService&quot;)
     * // any or no name specified is allowed
     * &#064;Override
     * public UserDetailsService userDetailsServiceBean() throws Exception {
     * 	return super.userDetailsServiceBean();
     * }
     * </pre>
     *
     * 重写此方法，以将通过{@link #configure（AuthenticationManagerBuilder）}创建的{@link #subjectDetailServiceBean()}公开为bean。
     *
     * To change the instance returned, developers should change
     * {@link #subjectDetailService()} ()} instead
     * @return
     * @throws Exception
     * @see #subjectDetailService()
     */
    public SubjectDetailService subjectDetailServiceBean() throws Exception {
        AuthenticationManagerBuilder globalAuthenticationManagerBuilder = context.getBean(AuthenticationManagerBuilder.class);
        return new SubjectDetailServiceDelegator(Arrays.asList(localAuthenticationManagerBuilder, globalAuthenticationManagerBuilder));
    }

    /**
     * 允许从{@link #subjectDetailServiceBean（）}修改和访问{@link #subjectDetailService()} ，
     * 而无需与{@link ApplicationContext}进行交互。 更改{@link #subjectDetailServiceBean（）}的实例时，开发人员应重写此方法。
     * @return
     */
    protected SubjectDetailService subjectDetailService() {
        AuthenticationManagerBuilder globalAuthBuilder = context.getBean(AuthenticationManagerBuilder.class);
        return new SubjectDetailServiceDelegator(Arrays.asList(localAuthenticationManagerBuilder, globalAuthBuilder));
    }

    @Override
    public void init(ChainProxyBuilder builder) throws Exception {
        final HttpSecurityBuilder httpSecurityBuilder = getHttpSecurityBuilder();
        builder.addSecurityFilterChainBuilder(httpSecurityBuilder).postBuildAction(new Runnable() {
            /**
             * 匿名内部类的方式设置后置处理线程任务
             *
             * 任务内容为:
             * 获取{@link FilterSecurityInterceptor}对象, 并将其注入到{@link ChainProxyBuilder #filterSecurityInterceptor}属性中
             */
            @Override
            public void run() {
                FilterSecurityInterceptor securityInterceptor = httpSecurityBuilder.getSharedObject(FilterSecurityInterceptor.class);
                builder.securityInterceptor(securityInterceptor);
            }
        });
    }

    /**
     * 重写此方法以配置{@link ChainProxyBuilder}。 例如，如果您希望忽略某些请求。
     * @param builder
     * @throws Exception
     */
    @Override
    public void configure(ChainProxyBuilder builder) throws Exception {
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        this.disabledLocalAuthenticationManagerBuilder = true;
    }

    /**
     * 留给子类重写的配置口子,
     * 用于配置{@link HttpSecurityBuilder}
     * 进行添加或修改过滤器
     *
     * 通常，子类不应通过调用super来调用此方法，因为它可能会覆盖其配置
     * @param builder
     */
    protected void configure(HttpSecurityBuilder builder) throws Exception {
        logger.debug("使用默认的configure（HttpSecurityBuilder）. 如果是子类，则可能会覆盖子类configure（HttpSecurityBuilder）.");
        builder.cors().and()
                .anonymous(securityContextHolder).and()
                .authorize().and()
                .securityContext(securityContextHolder).securityContextRepository(securityContextRepository).and()
                .formLogin(securityContextHolder).and()
                .exceptionTranslation(securityContextHolder);
    }

    /**
     * 获取 {@link #context}对象
     * @return
     */
    protected final ApplicationContext getApplicationContext(){
        return this.context;
    }

    /**
     * Delays the use of the {@link SubjectDetailService} from the
     * {@link AuthenticationManagerBuilder} to ensure that it has been fully configured.
     *
     *
     * 延迟{@link AuthenticationManagerBuilder}中{@link SubjectDetailService}的使用，以确保已完全配置。
     * @author Rob Winch
     * @since 3.2
     */
    static final class SubjectDetailServiceDelegator implements SubjectDetailService {

        private List<AuthenticationManagerBuilder> delegateBuilders;
        private SubjectDetailService delegate;
        private final Object delegateMonitor = new Object();

        SubjectDetailServiceDelegator(List<AuthenticationManagerBuilder> delegateBuilders) {
            if (delegateBuilders.contains(null)) {
                throw new IllegalArgumentException("delegateBuilders cannot contain null values. Got "+ delegateBuilders);
            }
            this.delegateBuilders = delegateBuilders;
        }

        @Override
        public SubjectDetail loadSubjectBySubjectName(String subjectName) throws SubjectNameNotFoundException {
            if (delegate != null) {
                return delegate.loadSubjectBySubjectName(subjectName);
            }

            synchronized (delegateMonitor) {
                if (delegate == null) {
                    for (AuthenticationManagerBuilder delegateBuilder : delegateBuilders) {
                        delegate = delegateBuilder.getDefaultSubjectDetailService();
                        if (delegate != null) {
                            break;
                        }
                    }

                    if (delegate == null) {
                        throw new IllegalStateException("SubjectDetailService is required.");
                    }
                    this.delegateBuilders = null;
                }
            }

            return delegate.loadSubjectBySubjectName(subjectName);
        }
    }

    /**
     * Delays the use of the {@link AuthenticationManager} build from the
     * {@link AuthenticationManagerBuilder} to ensure that it has been fully configured.
     *
     * 从{@link AuthenticationManagerBuilder}延迟使用{@link AuthenticationManager}构建，以确保已完全配置。
     * @author Rob Winch
     * @since 3.2
     */
    static final class AuthenticationManagerDelegator implements AuthenticationManager {
        private AuthenticationManagerBuilder delegateBuilder;
        private AuthenticationManager delegate;
        private final Object delegateMonitor = new Object();
        private Set<String> beanNames;

        AuthenticationManagerDelegator(AuthenticationManagerBuilder delegateBuilder, ApplicationContext context) {
            Assert.notNull(delegateBuilder, "delegateBuilder cannot be null");

            Field parentAuthMgrField = ReflectionUtils.findField(AuthenticationManagerBuilder.class, "parentAuthenticationManager");
            ReflectionUtils.makeAccessible(parentAuthMgrField);
            beanNames = getAuthenticationManagerBeanNames(context);
            validateBeanCycle(ReflectionUtils.getField(parentAuthMgrField, delegateBuilder), beanNames);
            this.delegateBuilder = delegateBuilder;
        }

        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            if (delegate != null) {
                return delegate.authenticate(authentication);
            }

            synchronized (delegateMonitor) {
                if (delegate == null) {
                    delegate = this.delegateBuilder.getTarget();
                    this.delegateBuilder = null;
                }
            }

            return delegate.authenticate(authentication);
        }

        private static Set<String> getAuthenticationManagerBeanNames(
                ApplicationContext applicationContext) {
            String[] beanNamesForType = BeanFactoryUtils
                    .beanNamesForTypeIncludingAncestors(applicationContext,
                            AuthenticationManager.class);
            return new HashSet<String>(Arrays.asList(beanNamesForType));
        }

        private static void validateBeanCycle(Object auth, Set<String> beanNames) {
            if (auth != null && !beanNames.isEmpty()) {
                if (auth instanceof Advised) {
                    Advised advised = (Advised) auth;
                    TargetSource targetSource = advised.getTargetSource();
                    if (targetSource instanceof LazyInitTargetSource) {
                        LazyInitTargetSource lits = (LazyInitTargetSource) targetSource;
                        if (beanNames.contains(lits.getTargetBeanName())) {
                            throw new FatalBeanException("" +
                                    "A dependency cycle was detected when trying to resolve the AuthenticationManager. Please ensure you have configured authentication.");
                        }
                    }
                }
                beanNames = Collections.emptySet();
            }
        }
    }
}
