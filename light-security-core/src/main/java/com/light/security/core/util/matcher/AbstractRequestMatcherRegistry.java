package com.light.security.core.util.matcher;

import com.light.security.core.config.core.ObjectPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName AbstractRequestMatcherRegistry
 * @Description 仿照SpringSecurity完成
 * A base class for registering {@link RequestMatcher}'s. For example, it might allow for
 * specifying which {@link RequestMatcher} require a certain level of authorization.
 *
 *
 * 翻译:
 * 用于注册{@link RequestMatcher}的基类。 例如，它可能允许指定哪个{@link RequestMatcher}需要一定级别的授权。
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public abstract class AbstractRequestMatcherRegistry<C> {

    private static final RequestMatcher ANY_REQUEST = AnyRequestMatcher.INSTANCE;

    private ApplicationContext context;

    protected final void setApplicationContext(ApplicationContext context){
        this.context = context;
    }

    protected final ApplicationContext getApplicationContext(){
        return this.context;
    }

    /**
     * 映射任何请求
     * @return
     */
    public C anyRequest(){
        return requestMatchers(ANY_REQUEST);
    }

    /**
     * 映射 {@link AntPathRequestMatcher}实例组成的List
     * @param httpMethod
     * @return
     */
    public C antMatchers(HttpMethod httpMethod){
        return antMatchers(httpMethod, new String[] { "/**" });
    }

    /**
     * 映射 {@link AntPathRequestMatcher}实例组成的List
     * @param httpMethod
     * @param antPatterns
     * @return
     */
    public C antMatchers(HttpMethod httpMethod, String... antPatterns){
        return chainRequestMatchers(RequestMatchers.antMatchers(httpMethod, antPatterns));
    }

    /**
     * 映射 {@link AntPathRequestMatcher}实例组成的List
     * @param antPatterns
     * @return
     */
    public C antMatchers(String... antPatterns){
        return chainRequestMatchers(RequestMatchers.antMatchers(antPatterns));
    }

    /**
     * <p>
     * Maps an {@link MvcRequestMatcher} that does not care which {@link HttpMethod} is
     * used. This matcher will use the same rules that Spring MVC uses for matching. For
     * example, often times a mapping of the path "/path" will match on "/path", "/path/",
     * "/path.html", etc.
     * </p>
     * <p>
     * If the current request will not be processed by Spring MVC, a reasonable default
     * using the pattern as a ant pattern will be used.
     * </p>
     *
     *
     * <p>
     * 映射一个不在乎使用哪个{@link HttpMethod}的{@link MvcRequestMatcher}。
     * 该匹配器将使用Spring MVC用于匹配的相同规则。
     * 例如，路径“ / path”的映射通常会在“ /path”、“/path/”、“/path.html”等上匹配。
     * </p>
     * <p>
     * 如果Spring MVC无法处理当前请求，则将使用该模式作为ant模式的合理默认值。
     * </p>
     * @param mvcPatterns
     * @return
     */
    public abstract C mvcMatchers(String... mvcPatterns);

    /**
     * <p>
     * Maps an {@link MvcRequestMatcher} that does not care which {@link HttpMethod} is
     * used. This matcher will use the same rules that Spring MVC uses for matching. For
     * example, often times a mapping of the path "/path" will match on "/path", "/path/",
     * "/path.html", etc.
     * </p>
     * <p>
     * If the current request will not be processed by Spring MVC, a reasonable default
     * using the pattern as a ant pattern will be used.
     * </p>
     *
     *
     * <p>
     * 映射一个不在乎使用哪个{@link HttpMethod}的{@link MvcRequestMatcher}。
     * 该匹配器将使用Spring MVC用于匹配的相同规则。
     * 例如，路径“ / path”的映射通常会在“ /path”、“/path/”、“/path.html”等上匹配。
     * </p>
     * <p>
     * 如果Spring MVC无法处理当前请求，则将使用该模式作为ant模式的合理默认值。
     * </p>
     * @param httpMethod
     * @param mvcPatterns
     * @return
     */
    public abstract C mvcMatchers(HttpMethod httpMethod, String... mvcPatterns);

    /**
     * 为传入的方法和匹配规则创建{@link MvcRequestMatcher}实例
     * @param method
     * @param mvcPatterns
     * @return
     */
    protected final List<MvcRequestMatcher> createMvcMatchers(HttpMethod method, String... mvcPatterns) {
        boolean isServlet30 = ClassUtils.isPresent("javax.servlet.ServletRegistration", getClass().getClassLoader());
        ObjectPostProcessor<Object> opp = this.context.getBean(ObjectPostProcessor.class);
        HandlerMappingIntrospector handlerMappingIntrospector = new HandlerMappingIntrospector();
        List<MvcRequestMatcher> matchers = new ArrayList<>(mvcPatterns.length);
        for (String mvcPattern : mvcPatterns) {
            MvcRequestMatcher matcher = new MvcRequestMatcher(handlerMappingIntrospector, mvcPattern);
            if (isServlet30) {
                opp.postProcess(matcher);
            }
            if (method != null) {
                matcher.setMethod(method);
            }
            matchers.add(matcher);
        }
        return matchers;
    }

    /**
     * 映射{@link RegexRequestMatcher}实例组成的List
     * @param method
     * @param regexPatterns
     * @return
     */
    public C regexMatchers(HttpMethod method, String... regexPatterns) {
        return chainRequestMatchers(RequestMatchers.regexMatchers(method, regexPatterns));
    }

    /**
     * 映射{@link RegexRequestMatcher}实例组成的List
     * @param regexPatterns
     * @return
     */
    public C regexMatchers(String... regexPatterns) {
        return chainRequestMatchers(RequestMatchers.regexMatchers(regexPatterns));
    }

    /**
     *
     * @param requestMatchers
     * @return
     */
    public C requestMatchers(RequestMatcher... requestMatchers) {
        return chainRequestMatchers(Arrays.asList(requestMatchers));
    }

    /**
     * 子类重写
     * @param requestMatchers
     * @return
     */
    protected abstract C chainRequestMatchers(List<RequestMatcher> requestMatchers);

    private static final class RequestMatchers{

        /**
         * Create a {@link List} of {@link AntPathRequestMatcher} instances.
         * @param httpMethod
         * @param antPatterns
         * @return
         */
        public static List<RequestMatcher> antMatchers(HttpMethod httpMethod, String... antPatterns){
            String method = httpMethod == null ? null : httpMethod.toString();
            List<RequestMatcher> matchers = new ArrayList<>();
            for (String pattern : antPatterns){
                matchers.add(new AntPathRequestMatcher(pattern, method));
            }
            return matchers;
        }

        public static List<RequestMatcher> antMatchers(String... antPatterns){
            return antMatchers(null, antPatterns);
        }

        public static List<RequestMatcher> regexMatchers(HttpMethod httpMethod, String... regexPatterns){
            String method = httpMethod == null ? null : httpMethod.toString();
            List<RequestMatcher> matchers = new ArrayList<>();
            for (String pattern : regexPatterns){
                matchers.add(new RegexRequestMatcher(pattern, method));
            }
            return matchers;
        }

        public static List<RequestMatcher> regexMatchers(String... regexPatterns){
            return regexMatchers(null, regexPatterns);
        }

        private RequestMatchers(){}
    }
}
