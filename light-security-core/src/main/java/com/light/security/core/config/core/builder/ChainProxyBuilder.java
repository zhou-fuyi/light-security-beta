package com.light.security.core.config.core.builder;

import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.config.core.AbstractConfiguredSecurityBuilder;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.SecurityBuilder;
import com.light.security.core.filter.FilterChainProxy;
import com.light.security.core.filter.FilterSecurityInterceptor;
import com.light.security.core.filter.chain.DefaultSecurityFilterChain;
import com.light.security.core.filter.chain.SecurityFilterChain;
import com.light.security.core.util.matcher.AbstractRequestMatcherRegistry;
import com.light.security.core.util.matcher.MvcRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ChainProxyBuilder
 * @Description 仿照SpringSecurity完成
 * <p>
 * The {@link ChainProxyBuilder} is created by {@link WebSecurityConfiguration} to create the
 * {@link FilterChainProxy} known as the Spring Security Filter Chain
 * (springSecurityFilterChain). The springSecurityFilterChain is the {@link Filter} that
 * the {@link DelegatingFilterProxy} delegates to.
 * </p>
 *
 * <p>
 * Customizations to the {@link ChainProxyBuilder} can be made by creating a
 * {@link com.light.security.core.config.core.WebSecurityConfigurer} or more likely by overriding
 * {@link com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter}.
 * </p>
 *
 * @see EnableWebSecurity
 * @see WebSecurityConfiguration
 *
 *
 * 翻译:
 * {@link ChainProxyBuilder}由{@link WebSecurityConfiguration}创建，以创建称为Spring Security
 * 过滤器链（springSecurityFilterChain）的{@link FilterChainProxy}。 springSecurityFilterChain
 * 是{@link DelegatingFilterProxy}委托给的{@link Filter}。
 * 可以通过创建{@link com.light.security.core.config.core.WebSecurityConfigurer}来进行{@link ChainProxyBuilder}的自定义
 * ，或者更有可能通过覆盖{@link com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter}来进行自定义。
 * @Author ZhouJian
 * @Date 2019-11-29
 */
public final class ChainProxyBuilder extends AbstractConfiguredSecurityBuilder<Filter, ChainProxyBuilder> implements SecurityBuilder<Filter>, ApplicationContextAware {

    private SecurityContextHolder securityContextHolder;

    /**
     * 用于保存{@link #ignoredRequestRegistry}注册的{@link RequestMatcher}
     */
    private List<RequestMatcher> ignoredRequests = new ArrayList<>();

    /**
     * 存储用于构建{@link SecurityFilterChain}的构建器对象
     */
    private final List<SecurityBuilder<? extends SecurityFilterChain>> securityFilterChainBuilders = new ArrayList<>();

    /**
     * 该对象包继承了{@link AbstractRequestMatcherRegistry 这是一个用于注册{@link RequestMatcher}的基类},
     * 包装了几个{@link RequestMatcher}的注册方法, 该实例仅仅用于注册{@link RequestMatcher}
     * 专门包装一个类来干这个事好像有点多余, 但是还是有必要的, 这个注册类是专门用于注册过滤器链需要忽略的请求<code> HttpServletRequest </code>
     *
     * 重点来了, 都说了这个类仅仅用于注册, 那么注册的需要忽略的{@link RequestMatcher}都是怎么保存的
     * 哈, 当然是使用我们上面定义的{@link #ignoredRequests}进行注册的{@link RequestMatcher}的保存。
     *
     */
    private IgnoredRequestRegistry ignoredRequestRegistry;

    /**
     * 授权过滤器
     */
    private FilterSecurityInterceptor filterSecurityInterceptor;

    /**
     * 后置处理线程任务, 这是一个线程对象
     * 待构建完成后会立即执行该Runnable
     */
    private Runnable postBuildAction = new Runnable() {
        @Override
        public void run() {
            logger.debug("Do nothing");
        }
    };

    public ChainProxyBuilder(ObjectPostProcessor<Object> objectPostProcessor, SecurityContextHolder securityContextHolder) {
        super(objectPostProcessor);
        this.securityContextHolder = securityContextHolder;
    }

    /**
     * <p>
     * Allows adding {@link RequestMatcher} instances that Spring Security
     * should ignore. Web Security provided by Spring Security (including the
     * {@link com.light.security.core.authentication.context.SecurityContext}) will not be available on {@link HttpServletRequest} that
     * match. Typically the requests that are registered should be that of only static
     * resources. For requests that are dynamic, consider mapping the request to allow all
     * users instead.
     * </p>
     *
     * Example Usage:
     *
     * <pre>
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /resources/ or /static/
     * 		.antMatchers(&quot;/resources/**&quot;, &quot;/static/**&quot;);
     * </pre>
     *
     * Alternatively this will accomplish the same result:
     *
     * <pre>
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /resources/ or /static/
     * 		.antMatchers(&quot;/resources/**&quot;).antMatchers(&quot;/static/**&quot;);
     * </pre>
     *
     * Multiple invocations of ignoring() are also additive, so the following is also
     * equivalent to the previous two examples:
     *
     * <pre>
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /resources/
     * 		.antMatchers(&quot;/resources/**&quot;);
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /static/
     * 		.antMatchers(&quot;/static/**&quot;);
     * // now both URLs that start with /resources/ and /static/ will be ignored
     * </pre>
     *
     *
     * 允许添加{@link RequestMatcher}实例，Spring Security应该忽略这些实例。
     * Spring Security提供的Web Security（包括{@link com.light.security.core.authentication.context.SecurityContext}）
     * 将无法在匹配的{@link HttpServletRequest}上使用。 通常，注册的请求应该仅是静态资源的请求。 对于动态请求，请考虑映射请求以允许所有用户使用。
     *
     * 该方法用于返回需要忽略的{@link RequestMatcher}的注册对象
     * @return the {@link IgnoredRequestRegistry} to use for registering request that
     * should be ignored
     */
    public IgnoredRequestRegistry ignoredRequestRegistry(){
        return ignoredRequestRegistry;
    }

    /**
     * 添加构建{@link SecurityFilterChain}的构建器实例
     * @param securityFilterChainBuilder
     * @return
     */
    public ChainProxyBuilder addSecurityFilterChainBuilder(SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder){
        this.securityFilterChainBuilders.add(securityFilterChainBuilder);
        return this;
    }

    /**
     * 设置{@link FilterSecurityInterceptor}。 通常由{@link com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter}调用。
     * @param securityInterceptor
     * @return
     */
    public ChainProxyBuilder securityInterceptor(FilterSecurityInterceptor securityInterceptor){
        this.filterSecurityInterceptor = securityInterceptor;
        return this;
    }

    /**
     * 配置后置处理线程任务
     * 构建完成后立即执行Runnable
     * @param postBuildAction
     * @return
     */
    public ChainProxyBuilder postBuildAction(Runnable postBuildAction){
        this.postBuildAction = postBuildAction;
        return this;
    }

    @Override
    protected Filter performBuild() throws Exception {
        /**
         * 先进行{@link #securityFilterChainBuilders}的校验, 如果为空集合则停止运行
         */
        Assert.state(!securityFilterChainBuilders.isEmpty(),
                "至少要给定一个SecurityBuilder<? extents SecurityFilterChain>, 通常是通过继承WebSecurityConfigurerAdapter并添加@Configuration来完成. 更高级的用户可以直接调用 "
                        + ChainProxyBuilder.class.getSimpleName() + ".addSecurityFilterChainBuilder() 方法来注入构架对象");

        // 即将进行过滤器链的创建, 可以有多个过滤器链
        // 提前计算过滤器链集合的容量, 避免动态扩容的消耗(因为要是用ArrayList啊)
        int chainSize = ignoredRequests.size() + securityFilterChainBuilders.size();
        List<SecurityFilterChain> securityFilterChains = new ArrayList<>(chainSize);
        /**
         * 需要忽略的的请求是通过过滤器链实现的, 因为当请求到达时会先进入一个代理类, 在此时会使用已有的过滤器链集合进行匹配,
         * 获取到匹配的过滤器链进行后续操作
         *
         * 这里注意:
         * 系统中可以存在至少一条或者多条过滤器链
         * 一条过滤器链可以零个或多个过滤器
         *
         * 重点又来了:
         * 这里需要留意的是, 需要忽略的{@link ignoredRequests}中, 一则{@link RequestMatcher}
         * 数据便对应一条过滤器链
         *
         * 下面便是使用需要忽略的{@link RequestMatcher}创建过滤器链
         */
        for (RequestMatcher matcher : ignoredRequests){
            securityFilterChains.add(new DefaultSecurityFilterChain(matcher));
        }

        /**
         * 系统中可以存在多条过滤器链的意义在于可以同时处理两条或多条业务线(相互之间不受影响), 比如同时作为资源服务器和认证服务器
         *
         * 这里是使用注入的构建器构建{@link SecurityFilterChain}, 默认情况下注入的构建对象为{@link HttpSecurityBuilder}类型,
         * 你也可以通过实现{@link FilterChainBuilder}接口实现自己的
         * <code> SecurityFilterChain 默认为 {@link DefaultSecurityFilterChain}</code>FilterChain构建器
         */
        for (SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder : securityFilterChainBuilders){
            securityFilterChains.add(securityFilterChainBuilder.build());
        }

        /**
         * 创建包装了List<SecurityFilterChain>的FilterChainProxy对象
         */
        FilterChainProxy filterChainProxy = new FilterChainProxy(securityFilterChains);
        filterChainProxy.setSecurityContextHolder(this.securityContextHolder);
//        filterChainProxy.genericInit();
        Filter target = filterChainProxy;
        postBuildAction.run();
        return target;
    }



    /**
     * An {@link IgnoredRequestRegistry} that allows optionally configuring the
     * {@link MvcRequestMatcher#setMethod(HttpMethod)}
     *
     * @author Rob Winch
     */
    public final class MvcMatchersIgnoredRequestRegistry extends IgnoredRequestRegistry {
        private final List<MvcRequestMatcher> mvcMatchers;

        private MvcMatchersIgnoredRequestRegistry(ApplicationContext context, List<MvcRequestMatcher> mvcMatchers) {
            super(context);
            this.mvcMatchers = mvcMatchers;
        }

        public IgnoredRequestRegistry servletPath(String servletPath) {
            for (MvcRequestMatcher matcher : this.mvcMatchers) {
                matcher.setServletPath(servletPath);
            }
            return this;
        }
    }

    /**
     * Allows registering {@link RequestMatcher} instances that should be ignored by
     * Spring Security.
     *
     * @author Rob Winch
     * @since 3.2
     */
    public class IgnoredRequestRegistry extends AbstractRequestMatcherRegistry<IgnoredRequestRegistry> {

        private IgnoredRequestRegistry(ApplicationContext context) {
            setApplicationContext(context);
        }

        @Override
        public MvcMatchersIgnoredRequestRegistry mvcMatchers(HttpMethod method, String... mvcPatterns) {
            List<MvcRequestMatcher> mvcMatchers = createMvcMatchers(method, mvcPatterns);
            ChainProxyBuilder.this.ignoredRequests.addAll(mvcMatchers);
            return new MvcMatchersIgnoredRequestRegistry(getApplicationContext(), mvcMatchers);
        }

        @Override
        public MvcMatchersIgnoredRequestRegistry mvcMatchers(String... mvcPatterns) {
            return mvcMatchers(null, mvcPatterns);
        }

        @Override
        protected IgnoredRequestRegistry chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            ChainProxyBuilder.this.ignoredRequests.addAll(requestMatchers);
            return this;
        }

        /**
         * Returns the {@link ChainProxyBuilder} to be returned for chaining.
         */
        public ChainProxyBuilder and() {
            return ChainProxyBuilder.this;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ignoredRequestRegistry = new IgnoredRequestRegistry(applicationContext);
    }

}
