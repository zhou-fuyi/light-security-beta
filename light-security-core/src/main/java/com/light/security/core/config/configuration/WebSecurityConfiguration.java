package com.light.security.core.config.configuration;

import com.light.security.core.config.AbstractSecurityWebApplicationInitializer;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.SecurityConfigurer;
import com.light.security.core.config.autowired.AutowiredWebSecurityConfigurersIgnoreParents;
import com.light.security.core.config.core.builder.ChainProxyBuilder;
import com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName WebSecurityConfiguration
 * @Description 用于加载并启动 {@link com.light.security.core.filter.FilterChainProxy}
 * @Author ZhouJian
 * @Date 2019-12-04
 */
@Configuration
public class WebSecurityConfiguration implements BeanClassLoaderAware {

    private ChainProxyBuilder chainProxyBuilder;

    private List<SecurityConfigurer<Filter, ChainProxyBuilder>> chainProxyBuilderConfigurers;

    private ClassLoader beanClassLoader;

    @Autowired(required = false)
    private ObjectPostProcessor<Object> objectPostProcessor;

    /**
     * 在这里初始化了AutowiredWebSecurityConfigurersIgnoreParents并交由Spring进行管理
     * @param configurableListableBeanFactory
     * @return
     */
    @Bean
    public AutowiredWebSecurityConfigurersIgnoreParents autowiredWebSecurityConfigurersIgnoreParents(ConfigurableListableBeanFactory configurableListableBeanFactory){
        return new AutowiredWebSecurityConfigurersIgnoreParents(configurableListableBeanFactory);
    }

    /**
     * 核心方法
     * 创建并启动{@link com.light.security.core.filter.FilterChainProxy}
     * @return
     * @throws Exception
     */
    @Bean(name = AbstractSecurityWebApplicationInitializer.DEFAULT_LIGHT_SECURITY_FILTER_NAME)
    public Filter lightSecurityFilterChain() throws Exception {
        boolean hacConfigurers = !CollectionUtils.isEmpty(chainProxyBuilderConfigurers);
        // 如果webSecurityConfigurers为空, 则会创建默认的配置器
        if (!hacConfigurers){
            // WebSecurityConfigurerAdapter匿名内部类实现
            WebSecurityConfigurerAdapter defaultConfigurer = objectPostProcessor.postProcess(new WebSecurityConfigurerAdapter() {
            });
            chainProxyBuilder.apply(defaultConfigurer);
        }
        return chainProxyBuilder.build();
    }

    /**
     * 创建{@link #chainProxyBuilder}
     * 收集自定义的WebSecurityConfigurer的子类实例并配置到创建的{@link ChainProxyBuilder}实例中
     * @param objectPostProcessor
     * @param webSecurityConfigurers
     */
    @Autowired(required = false)
    public void setChainProxyBuilderConfigurers(
            ObjectPostProcessor<Object> objectPostProcessor,
            @Value("#{@autowiredWebSecurityConfigurersIgnoreParents.getWebSecurityConfigurers()}") List<SecurityConfigurer<Filter, ChainProxyBuilder>> webSecurityConfigurers) throws Exception {

        // 创建一个chainProxyBuilder对象
        chainProxyBuilder = objectPostProcessor.postProcess(new ChainProxyBuilder(objectPostProcessor));

        //对webSecurityConfigurers进行排序
        Collections.sort(webSecurityConfigurers, AnnotationAwareOrderComparator.INSTANCE);

        //对Order进行比较是否存在相同的,如此简单的比较是在经过排序的前提下进行的
        Integer previousOrder = null;
        Object previousConfig = null;
        for (SecurityConfigurer<Filter, ChainProxyBuilder> config : webSecurityConfigurers) {
            Integer order = AnnotationAwareOrderComparator.lookupOrder(config);
            /**
             * 在提前排过序的情况下, 只需要比较相邻的两个元素即可
             */
            if (previousOrder != null && previousOrder.equals(order)) {
                throw new IllegalStateException(
                        "@Order on WebSecurityConfigurers must be unique. Order of "
                                + order + " was already used on " + previousConfig + ", so it cannot be used on "
                                + config + " too.");
            }
            previousOrder = order;
            previousConfig = config;
        }
        for (SecurityConfigurer<Filter, ChainProxyBuilder> webSecurityConfigurer : webSecurityConfigurers) {
            // 将配置器信息配置到 AbstractConfiguredSecurityBuilder 中
            chainProxyBuilder.apply(webSecurityConfigurer);
        }
        this.chainProxyBuilderConfigurers = webSecurityConfigurers; // 将方法内的局部变量赋值给类成员变量
    }

    /**
     * 注解排序器, 主要是应用于{@link Order}
     */
    private static class AnnotationAwareOrderComparator extends OrderComparator {

        private static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

        /**
         * 这个方法返回值是比较的关键, 这里其实就是根据Order值排序
         * @param obj
         * @return
         */
        @Override
        protected int getOrder(Object obj) {
            return lookupOrder(obj);
        }

        private static int lookupOrder(Object obj) {
            if (obj instanceof Ordered) {
                return ((Ordered) obj).getOrder();
            }
            if (obj != null) {
                Class<?> clazz = (obj instanceof Class ? (Class<?>) obj : obj.getClass());
                Order order = AnnotationUtils.findAnnotation(clazz, Order.class);
                if (order != null) {
                    return order.value();
                }
            }
            /**
             * 如果获取{@code @Order(value)}失败, 则返回默认值{@link Integer.MAX_VALUE}
             */
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
}
