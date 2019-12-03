package com.light.security.core.config.core.builder;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.AuthenticationManager;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.config.core.AbstractConfiguredSecurityBuilder;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.SecurityBuilder;
import com.light.security.core.config.core.SecurityConfigurerAdapter;
import com.light.security.core.config.core.configurer.CorsConfigurer;
import com.light.security.core.config.core.configurer.ExceptionTranslationConfigurer;
import com.light.security.core.config.core.configurer.SecurityContextConfigurer;
import com.light.security.core.filter.chain.DefaultSecurityFilterChain;
import com.light.security.core.util.matcher.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName HttpSecurityBuilder
 * @Description {@link FilterChainBuilder}的直接实现, 用于构建{@link DefaultSecurityFilterChain}
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class HttpSecurityBuilder extends AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurityBuilder> implements
        SecurityBuilder<DefaultSecurityFilterChain>, FilterChainBuilder<HttpSecurityBuilder> {

    /**
     * 内部类方式实现
     */
    private final RequestMatcherConfigurer requestMatcherConfigurer;

    private List<Filter> filters = new ArrayList<>();
    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
    private FilterComparator comparator = new FilterComparator();


    public HttpSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor, AuthenticationManagerBuilder authenticationManagerBuilder, Map<Class<? extends Object>, Object> sharedObjects) {
        super(objectPostProcessor);
        Assert.notNull(authenticationManagerBuilder, "AuthenticationManagerBuilder 不能为null");
        setSharedObject(AuthenticationManagerBuilder.class, authenticationManagerBuilder);
        for (Map.Entry<Class<? extends Object>, Object> entry : sharedObjects.entrySet()){
            setSharedObject((Class<Object>) entry.getKey(), entry.getValue());
        }

        ApplicationContext context = (ApplicationContext) sharedObjects.get(ApplicationContext.class);
        this.requestMatcherConfigurer = new RequestMatcherConfigurer(context);//内部类实现
    }

    private ApplicationContext getContext(){
        return this.getSharedObject(ApplicationContext.class);
    }

    /**
     * 此时开启的CorsFilter没有配置<code> CorsConfigurationSource </code>
     * @return
     * @throws Exception
     */
    public CorsConfigurer<HttpSecurityBuilder> cors() throws Exception{
        return getOrApply(new CorsConfigurer<HttpSecurityBuilder>());
    }

    /**
     * 配置{@link com.light.security.core.filter.SecurityContextPretreatmentFilter}过滤器
     * @param securityContextHolder
     * @return
     * @throws Exception
     */
    public SecurityContextConfigurer<HttpSecurityBuilder> securityContext(SecurityContextHolder securityContextHolder) throws Exception {
        return getOrApply(new SecurityContextConfigurer<HttpSecurityBuilder>(securityContextHolder));
    }

    /**
     * 配置{@link com.light.security.core.filter.ExceptionTranslationFilter}过滤器
     * @param securityContextHolder
     * @return
     * @throws Exception
     */
    public ExceptionTranslationConfigurer<HttpSecurityBuilder> exceptionTranslation(SecurityContextHolder securityContextHolder) throws Exception {
        return getOrApply(new ExceptionTranslationConfigurer<HttpSecurityBuilder>(securityContextHolder));
    }

    @Override
    protected void beforeConfigure() throws Exception {
        /**
         * 创建AuthenticationManager
         */
        setSharedObject(AuthenticationManager.class, getAuthenticationRegistry().build());
    }

    private AuthenticationManagerBuilder getAuthenticationRegistry() {
        return getSharedObject(AuthenticationManagerBuilder.class);
    }

    @Override
    protected DefaultSecurityFilterChain performBuild() throws Exception {
        Collections.sort(filters, comparator);
        return new DefaultSecurityFilterChain(requestMatcher, filters);
    }

    @Override
    public HttpSecurityBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
        /**
         * 给 AuthenticationManagerBuilder 设置 AuthenticationProvider
         */
        getAuthenticationRegistry().authenticationProvider(authenticationProvider);
        return this;
    }

    @Override
    public HttpSecurityBuilder subjectDetailService(SubjectDetailService subjectDetailService) throws Exception {
        /**
         * 给 AuthenticationManagerBuilder 设置 SubjectDetailService
         */
        getAuthenticationRegistry().subjectDetailService(subjectDetailService);
        return this;
    }

    /**
     * 在afterFilter后插入一个filter
     * @param filter
     * @param afterFilter
     * @return
     */
    @Override
    public HttpSecurityBuilder addFilterAfter(Filter filter, Class<? extends Filter> afterFilter) {
        comparator.registerAfter(filter.getClass(), afterFilter);
        return addFilter(filter);
    }

    /**
     * 在 beforeFilter前插入一个Filter
     * @param filter
     * @param beforeFilter
     * @return
     */
    @Override
    public HttpSecurityBuilder addFilterBefore(Filter filter, Class<? extends Filter> beforeFilter) {
        comparator.registerBefore(filter.getClass(), beforeFilter);
        return addFilter(filter);
    }

    @Override
    public HttpSecurityBuilder addFilter(Filter filter) {
        Class<? extends Filter> filterClass = filter.getClass();
        /**
         * 没有在{@link FilterComparator}中注册的不能直接使用addFilter进行注册,
         * 需要使用addFilter**(比如: addFilterBefore, addFilterAfter)的方式进行
         */
        if (!comparator.isRegistered(filterClass)){
            throw new IllegalArgumentException("该Filter类: " + filterClass.getName() + "没有被预定义, 无法直接进行注册. 考虑改用addFilterAfter或addFilterBefore方法进行注册.");
        }
        this.filters.add(filter);
        return this;
    }

    public HttpSecurityBuilder addFilterAt(Filter filter, Class<? extends Filter> atFilter){
        this.comparator.registerAt(filter. getClass(), atFilter);
        return addFilter(filter);
    }

    /**
     * 设置过滤器链的匹配器
     * @param requestMatcher
     * @return
     */
    public HttpSecurityBuilder requestMatcher(RequestMatcher requestMatcher){
        this.requestMatcher = requestMatcher;
        return this;
    }

    public HttpSecurityBuilder antMatcher(String antPattern){
        return requestMatcher(new AntPathRequestMatcher(antPattern));
    }

    public HttpSecurityBuilder mvcMatcher(String mvcPattern){
        HandlerMappingIntrospector handlerMappingIntrospector = new HandlerMappingIntrospector();
        return requestMatcher(new MvcRequestMatcher(handlerMappingIntrospector, mvcPattern));
    }

    public HttpSecurityBuilder regexMatcher(String regexPattern){
        return requestMatcher(new RegexRequestMatcher(regexPattern, null));
    }

    public RequestMatcherConfigurer requestMatcherConfigurer(){
        return this.requestMatcherConfigurer;
    }

    /**
     * If the {@link com.light.security.core.config.core.SecurityConfigurer} has already been specified get the original,
     * otherwise apply the new {@link SecurityConfigurerAdapter}.
     *
     * 如果已经指定了{@link com.light.security.core.config.core.SecurityConfigurer}，请获取原始的，否则应用新的{@link SecurityConfigurerAdapter}。
     * @param configurer
     * @param <C>
     * @return
     * @throws Exception
     */
    private <C extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurityBuilder>> C getOrApply(C configurer) throws Exception {
        C existed = (C) getConfigurer(configurer.getClass());
        if (existed != null){
            return existed;
        }
        return apply(configurer);
    }

    /**
     * An extension to {@link RequestMatcherConfigurer} that allows optionally configuring
     * the servlet path.
     *
     * {@link RequestMatcherConfigurer}的扩展，可以选择配置servlet路径。
     *
     * @author Rob Winch
     */
    public final class MvcMatchersRequestMatcherConfigurer extends RequestMatcherConfigurer {

        /**
         * Creates a new instance
         * @param context the {@link ApplicationContext} to use
         * @param matchers the {@link MvcRequestMatcher} instances to set the servlet path
         * on if {@link #servletPath(String)} is set.
         */
        private MvcMatchersRequestMatcherConfigurer(ApplicationContext context, List<MvcRequestMatcher> matchers) {
            super(context);
            this.matchers = new ArrayList<RequestMatcher>(matchers);
        }

        public RequestMatcherConfigurer servletPath(String servletPath) {
            for (RequestMatcher matcher : this.matchers) {
                ((MvcRequestMatcher) matcher).setServletPath(servletPath);
            }
            return this;
        }

    }

    /**
     * 允许映射此{@link HttpSecurityBuilder}将用于的HTTP请求
     */
    public class RequestMatcherConfigurer extends AbstractRequestMatcherRegistry<RequestMatcherConfigurer> {

        protected List<RequestMatcher> matchers = new ArrayList<RequestMatcher>();

        /**
         * @param context
         */
        private RequestMatcherConfigurer(ApplicationContext context) {
            setApplicationContext(context);
        }

        @Override
        public RequestMatcherConfigurer mvcMatchers(String... mvcPatterns) {
            return mvcMatchers(null, mvcPatterns);
        }

        @Override
        public RequestMatcherConfigurer mvcMatchers(HttpMethod httpMethod, String... mvcPatterns) {
            List<MvcRequestMatcher> mvcRequestMatchers = createMvcMatchers(httpMethod, mvcPatterns);

            return null;
        }

        @Override
        protected RequestMatcherConfigurer chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            return null;
        }

        private void setMatchers(List<? extends RequestMatcher> requestMatchers){
            this.matchers.addAll(requestMatchers);
            requestMatchers(new OrRequestMatcher(this.matchers));
        }

        public HttpSecurityBuilder and(){
            return HttpSecurityBuilder.this;
        }
    }
}
