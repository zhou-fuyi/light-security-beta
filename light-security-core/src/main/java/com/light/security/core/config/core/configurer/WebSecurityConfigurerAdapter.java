package com.light.security.core.config.core.configurer;

import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.AuthenticationTrustResolver;
import com.light.security.core.authentication.AuthenticationTrustResolverImpl;
import com.light.security.core.authentication.DefaultAuthenticationEventPublisher;
import com.light.security.core.config.configuration.AuthenticationConfiguration;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.WebSecurityConfigurer;
import com.light.security.core.config.core.builder.AuthenticationManagerBuilder;
import com.light.security.core.config.core.builder.ChainProxyBuilder;
import com.light.security.core.config.core.builder.HttpSecurityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.SpringFactoriesLoader;

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
    private boolean disableDefaults;

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

    @Override
    public void configure(ChainProxyBuilder builder) throws Exception {
        this.disabledLocalAuthenticationManagerBuilder = true;
    }

    protected final HttpSecurityBuilder getHttpSecurityBuilder() throws Exception {
        if (httpSecurityBuilder != null){
            return httpSecurityBuilder;
        }

        DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor.postProcess(new DefaultAuthenticationEventPublisher());
        //设置事件发布器
        localAuthenticationManagerBuilder.authenticationEventPublisher(eventPublisher);
        
        AuthenticationManager authenticationManager = authenticationManager();

        // TODO: 2019-12-03 顺序往下, 到这里了 
        return null;
    }

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
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        this.disabledLocalAuthenticationManagerBuilder = true;
    }
}
