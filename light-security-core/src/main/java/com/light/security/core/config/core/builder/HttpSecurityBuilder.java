package com.light.security.core.config.core.builder;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.config.core.AbstractConfiguredSecurityBuilder;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.SecurityBuilder;
import com.light.security.core.filter.chain.DefaultSecurityFilterChain;
import com.light.security.core.util.matcher.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HttpSecurityBuilder
 * @Description {@link FilterChainBuilder}的直接实现, 用于构建{@link DefaultSecurityFilterChain}
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class HttpSecurityBuilder extends AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurityBuilder> implements
        SecurityBuilder<DefaultSecurityFilterChain>, FilterChainBuilder<HttpSecurityBuilder> {

    private final RequestMatcherConfigurer requestMatcherConfigurer;
    private List<Filter> filters = new ArrayList<>();
    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
    private FilterComparator comparator = new FilterComparator();


    public HttpSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor, AuthenticationMa) {
        super(objectPostProcessor);
    }


    @Override
    protected DefaultSecurityFilterChain performBuild() throws Exception {
        return null;
    }

    @Override
    public HttpSecurityBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
        return null;
    }

    @Override
    public HttpSecurityBuilder subjectDetailService(SubjectDetailService subjectDetailService) throws Exception {
        return null;
    }

    @Override
    public HttpSecurityBuilder addFilterAfter(Filter filter, Class<? extends Filter> afterFilter) {
        return null;
    }

    @Override
    public HttpSecurityBuilder addFilterBefore(Filter filter, Class<? extends Filter> beforeFilter) {
        return null;
    }

    @Override
    public HttpSecurityBuilder addFilter(Filter filter) {
        return null;
    }

    public HttpSecurityBuilder requestMatchers(RequestMatcher requestMatcher){
        this.requestMatcher = requestMatcher;
        return this;
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
