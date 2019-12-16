package com.light.security.core.filter;

import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.filter.chain.SecurityFilterChain;
import com.light.security.core.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName FilterChainProxy
 * @Description 核心过滤器之一 <code> FilterChainProxy </code>, 译为 过滤器链代理
 * 该过滤器中持有{@link com.light.security.core.filter.chain.SecurityFilterChain}的集合, 即为多个<code> SecurityFilterChain </code>
 *
 * 该类作用: 该过滤器提供根据当前请求选择过滤器链进而执行过滤器链的功能
 *
 * <code> FilterChainProxy </code>的执行依赖于另一个代理类{@link org.springframework.web.filter.DelegatingFilterProxy},
 * 这个代理类<code> DelegatingFilterProxy </code>是一个标准Servlet Filter的代理, 通过这个代理将后续执行委派给实现了过滤器接口{@link javax.servlet.Filter}的Spring Bean.
 * 关于这个代理类的更多介绍可以参考: https://www.baeldung.com/spring-delegating-filter-proxy
 *
 * 小声BB:
 * {@link org.springframework.web.filter.DelegatingFilterProxy}虽然是一个代理类, 但是也是一个名副其实的Servlet Filter Bean, 它是需要注册到ServletContainer中的,
 * 其生命周期受Servlet容器管理. 而像Light Security中使用的Filter Bean(实现了{@link javax.servlet.Filter}接口的Spring Bean)
 * (    例如:
 *          {@link SubjectNamePasswordAuthenticationFilter}
 *          {@link FilterChainProxy}
 * )是没有注册到ServletContainer中的, 这里所提到的Filter Bean默认情况下是有Spring进行生命周期的管理, 由{@link org.springframework.web.filter.DelegatingFilterProxy}
 * 代理接入标准Servlet Filter, 在该代理类的doFilter方法中委派给 "自定义的Filter Bean" 进行后续动作. 这些 "自定义的Filter Bean" 是在Spring环境下, 是可以使用Spring
 * 带来的相关功能, 比如依赖注入.
 * @Author ZhouJian
 * @Date 2019-12-04
 */
public class FilterChainProxy extends GenericFilter {

    private static final Logger logger = LoggerFactory.getLogger(FilterChainProxy.class);

    private SecurityContextHolder securityContextHolder;

    /**
     * 内部维护一组过滤器链, 是后续执行的过滤器源
     */
    private List<SecurityFilterChain> securityFilterChains;

    /**
     * 过滤器链检查器, 默认不做检查
     */
    private FilterChainValidator filterChainValidator = new NullFilterChainValidator();

    public FilterChainProxy(){}

    public FilterChainProxy(SecurityFilterChain chain){
        this(Arrays.asList(chain));
    }

    public FilterChainProxy(List<SecurityFilterChain> chains){
        Assert.notEmpty(chains, "构造器接收空值参数");
        this.securityFilterChains = chains;
    }

    @Override
    public void genericInit() {
        Assert.notNull(securityContextHolder, "SecurityContextHolder 不能为null");
        filterChainValidator.validate(this);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 暂时不做什么优化
        try {
            doFilterInternal(request, response, chain);
        }finally {
            securityContextHolder.cleanContext();
        }
    }

    /**
     * 核心执行流程
     * 获取当前请求匹配的一组过滤器
     * 根据这组过滤器创建一个虚拟的过滤器链, 注意这里的虚拟过滤器链与原始链的区别
     * 执行虚拟过滤器链的流程, 待虚拟过滤器链执行完毕后回归原始链进行后续执行
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    private void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest internalRequest = (HttpServletRequest) request;
        HttpServletResponse internalResponse = (HttpServletResponse) response;
        List<Filter> filters = getFilters(internalRequest);

        if (CollectionUtils.isEmpty(filters)){
            if (logger.isDebugEnabled()){
                logger.debug("请求: {} --> {}", ServletUtils.getServletPath(internalRequest), (filters == null ? " 没有匹配的过滤器": " 过滤器列表为空"));
            }

            chain.doFilter(internalRequest, internalResponse);
            return;
        }
        /**
         * 创建虚拟链并执行
         */
        VirtualFilterChain virtualFilterChain = new VirtualFilterChain(chain, filters);
        virtualFilterChain.doFilter(internalRequest, internalResponse);
    }

    private List<Filter> getFilters(HttpServletRequest request) {
        for (SecurityFilterChain chain : this.securityFilterChains){
            if (chain.matches(request)){
                return chain.getFilters();
            }
        }
        return null;
    }

    public List<Filter> getFilters(String url) {
        for (SecurityFilterChain chain : securityFilterChains){
            /**
             * 仿照SpringSecurity中FilterInvocation创建DummyRequest
             */
            if (chain.matches(new HttpServletRequestWrapper((HttpServletRequest) Proxy.newProxyInstance(HttpServletRequestWrapper.class.getClassLoader()
                    , new Class[] { HttpServletRequest.class }
                    , new UnsupportedOperationExceptionInvocationHandler())){
                @Override
                public String getServletPath() {
                    return url;
                }
            })){
                return chain.getFilters();
            }
        }
        return null;
    }

    public List<SecurityFilterChain> getFilterChains(){
        return Collections.unmodifiableList(this.securityFilterChains);
    }

    public void setFilterChainValidator(FilterChainValidator filterChainValidator){
        Assert.notNull(filterChainValidator, "FilterChainValidator 不能为null");
        this.filterChainValidator = filterChainValidator;
    }

    public void setSecurityContextHolder(SecurityContextHolder securityContextHolder) {
        this.securityContextHolder = securityContextHolder;
    }

    /**
     * 过滤器链检查器, 可以在初始化完成后进行一些检查
     */
    public interface FilterChainValidator{
        void validate(FilterChainProxy filterChainProxy);
    }

    private static class NullFilterChainValidator implements FilterChainValidator{
        @Override
        public void validate(FilterChainProxy filterChainProxy) {
            logger.debug("Do noting, 你可以实现 FilterChainValidator 接口完成自己的检查逻辑");
        }
    }

    /**
     * 核心:
     *  内部{@code FilterChain}实现，用于通过与请求匹配的其他内部过滤器列表传递请求。
     *
     * 参照SpringSecurity实现
     * 当时本想着把这个内部类提出去单独作为一个类文件, 但是想着这样破坏了原有的封装,
     * 所以还是使用私有的内部类的方式
     */
    private static class VirtualFilterChain implements FilterChain {

        private final FilterChain originChain;//用于记录标准的Servlet FilterChain中的FilterChain
        private final List<Filter> additionFilters;//用于保存传入过滤器链
        private final int size;//用于记录传入过滤器链的大小
        private int currentPosition;//用于记录过滤器链中Filter的执行位置

        private VirtualFilterChain(FilterChain originChain, List<Filter> additionFilters){
            this.originChain = originChain;
            this.additionFilters = additionFilters;
            this.size = additionFilters.size();
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (currentPosition == size){
                if (logger.isDebugEnabled()){
                    logger.debug(ServletUtils.getServletPath((HttpServletRequest) request) + " 已经到达附加过滤器链的末端； 继续原始链");
                }

                //继续原始链
                originChain.doFilter(request, response);
            }else {
                /**
                 * 这里是先加一, 所以上面的判断条件是 <code> currentPosition == size</code>
                 */
                currentPosition++;

                Filter nextFilter = additionFilters.get(currentPosition - 1);//所以这里要减一

                if (logger.isDebugEnabled()){
                    logger.debug("{} 已执行到附加过滤器链的第 {} 个位置, 共有 {} 个过滤器, 当前作用的过滤器为: {}", ServletUtils.getServletPath((HttpServletRequest) request)
                            , currentPosition, size, nextFilter.getClass().getSimpleName());
                }
                nextFilter.doFilter(request, response, this);
            }
        }
    }

    private final class UnsupportedOperationExceptionInvocationHandler implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            throw new UnsupportedOperationException(method + " is not supported");
        }
    }
}
