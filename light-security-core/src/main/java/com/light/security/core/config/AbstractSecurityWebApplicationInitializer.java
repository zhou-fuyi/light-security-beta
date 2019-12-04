package com.light.security.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.*;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * @ClassName AbstractSecurityWebApplicationInitializer
 * @Description 用于向 {@link ServletContext}注册Servlet Filter, 这里是外置Tomcat启动加载Servlet相关Bean的实现路子, 这里也是为了兼容
 * @Author ZhouJian
 * @Date 2019-12-04
 */
public abstract class AbstractSecurityWebApplicationInitializer implements WebApplicationInitializer {

    private static final String SERVLET_CONTEXT_PREFIX = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.";

    public static final String DEFAULT_LIGHT_SECURITY_FILTER_NAME = "light_security_filter_chain";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        beforeLightSecurityFilterChain(servletContext);
        insertSpringSecurityFilterChain(servletContext);
        afterLightSecurityFilterChain(servletContext);
    }

    /**
     * 注册Servlet Filter, 这里注册的是{@link DelegatingFilterProxy}
     * @param servletContext
     */
    private void insertSpringSecurityFilterChain(ServletContext servletContext){
        String filterName = DEFAULT_LIGHT_SECURITY_FILTER_NAME;
        DelegatingFilterProxy lightSecurityFilterChain = new DelegatingFilterProxy(filterName);
        String contextAttribute = getWebApplicationContextAttribute();
        if (contextAttribute != null) {
            lightSecurityFilterChain.setContextAttribute(contextAttribute);
        }
        registerFilter(servletContext, true, filterName, lightSecurityFilterChain);
    }

    protected abstract void afterLightSecurityFilterChain(ServletContext servletContext);

    protected abstract void beforeLightSecurityFilterChain(ServletContext servletContext);


    protected final void insertFilters(ServletContext servletContext, Filter... filters) {
        registerFilters(servletContext, true, filters);
    }

    protected final void appendFilters(ServletContext servletContext, Filter... filters) {
        registerFilters(servletContext, false, filters);
    }

    protected String getDispatcherWebApplicationContextSuffix() {
        return null;
    }

    private String getWebApplicationContextAttribute() {
        String dispatcherServletName = getDispatcherWebApplicationContextSuffix();
        if (dispatcherServletName == null) {
            return null;
        }
        return SERVLET_CONTEXT_PREFIX + dispatcherServletName;
    }

    private void registerFilters(ServletContext servletContext, boolean insertBeforeOtherFilters, Filter... filters) {
        Assert.notEmpty(filters, "filters cannot be null or empty");
        for (Filter filter : filters) {
            if (filter == null) {
                throw new IllegalArgumentException("filters cannot contain null values. Got " + Arrays.asList(filters));
            }
            String filterName = Conventions.getVariableName(filter);
            registerFilter(servletContext, insertBeforeOtherFilters, filterName, filter);
        }
    }

    /**
     * 使用Servlet3.0新特性, 动态注册Servlet Filter
     * @param servletContext
     * @param insertBeforeOtherFilters
     * @param filterName
     * @param filter
     */
    private final void registerFilter(ServletContext servletContext, boolean insertBeforeOtherFilters, String filterName, Filter filter) {
        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(filterName, filter);
        if (filterRegistration == null){
            throw new IllegalStateException( "Duplicate Filter registration for '" + filterName + "'. Check to ensure the Filter is only configured once. (重复注册检查)");
        }
        filterRegistration.setAsyncSupported(isAsyncSecuritySupported());
        EnumSet<DispatcherType> dispatcherTypes = getSecurityDispatcherTypes();
        filterRegistration.addMappingForUrlPatterns(dispatcherTypes, !insertBeforeOtherFilters, "/*");
    }

    protected EnumSet<DispatcherType> getSecurityDispatcherTypes(){
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC);
    }

    /**
     * 确认 light_security_filter_chain 是否支持异步, 默认返回true
     * @return
     */
    protected boolean isAsyncSecuritySupported(){
        return true;
    }
}
