package com.light.security.core.filter;

import com.light.security.core.access.AuthorizationStatusToken;
import com.light.security.core.access.FilterInvocation;
import com.light.security.core.access.meta.FilterInvocationSecurityMetadataSource;
import com.light.security.core.access.meta.SecurityMetadataSource;

import javax.servlet.*;
import java.io.IOException;

/**
 * @ClassName FilterSecurityInterceptor
 * @Description 授权过滤器
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class FilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    private static final String FILTER_APPLIED = "__light_security_filterSecurityInterceptor_filterApplied";

    //权限数据源
    private FilterInvocationSecurityMetadataSource securityMetadataSource;
    //每个请求只做一次检查
    private boolean observeOncePerRequest = true;

    @Override
    protected SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterInvocation invocation = new FilterInvocation(request, response, chain);
        invoke(invocation);
    }

    @Override
    public void destroy() {

    }

    /**
     * 过滤器代理实现
     * @param invocation
     * @throws IOException
     * @throws ServletException
     */
    private void invoke(FilterInvocation invocation) throws IOException, ServletException {
        // 对同一个请求的多次访问则放行 这里的注释可以参见: {@link https://www.cnblogs.com/question-sky/p/7065808.html}
        if (observeOncePerRequest && (invocation.getRequest() != null) && (invocation.getRequest().getAttribute(FILTER_APPLIED) != null)){
            // filter already applied to this request and user wants us to observe
            // once-per-request handling, so don't re-do security checking
            // 过滤器已应用于此请求，并且用户希望我们观察每个请求一次的处理，因此请勿重新进行安全检查
            invocation.getChain().doFilter(invocation.getRequest(), invocation.getResponse());
        }else {
            // 第一次访问则需要拦截验证
            if (invocation.getRequest() != null){
                invocation.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
            }
            AuthorizationStatusToken token = beforeInvocation(invocation);
            try {
                invocation.getChain().doFilter(invocation.getRequest(), invocation.getResponse());
            }finally {
                finallyInvocation(token);
            }
//        afterInvocation(token, null);
        }
    }

    public FilterInvocationSecurityMetadataSource getSecurityMetadataSource() {
        return securityMetadataSource;
    }

    public void setSecurityMetadataSource(FilterInvocationSecurityMetadataSource securityMetadataSource) {
        this.securityMetadataSource = securityMetadataSource;
    }

    public boolean isObserveOncePerRequest() {
        return observeOncePerRequest;
    }

    public void setObserveOncePerRequest(boolean observeOncePerRequest) {
        this.observeOncePerRequest = observeOncePerRequest;
    }
}
