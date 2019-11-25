package com.light.security.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.Serializable;
import java.util.Enumeration;

/**
 * @ClassName GenericFilter
 * @Description 通用的Filter, 之后的所有自定义Filter基本上都将继承该类
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public abstract class GenericFilter implements Filter, FilterConfig, InitializingBean, Serializable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    //Ant风格URL匹配器
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    protected volatile FilterConfig filterConfig;

    //当前过滤器作用的Url组
    protected String[] processUrl;

    public static AntPathMatcher getAntPathMatcher() {
        return ANT_PATH_MATCHER;
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public String[] getProcessUrl() {
        return processUrl;
    }

    public void setProcessUrl(String... processUrl) {
        logger.info("当前String多参数类型为: {}", processUrl.getClass().getName());
        this.processUrl = processUrl;
    }

    /**
     * 可以自定义一些装配工作
     */
    protected void genericInit(){}

    @Override
    public String getInitParameter(String name) {
        return getFilterConfig().getInitParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return getFilterConfig().getInitParameterNames();
    }

    @Override
    public ServletContext getServletContext() {
        return getFilterConfig().getServletContext();
    }

    @Override
    public String getFilterName() {
        return getFilterConfig().getFilterName();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        if (logger.isDebugEnabled()){
            logger.debug("{} 初始化 ...", this.getClass().getName());
        }
    }

    @Override
    public void destroy() {
        if (logger.isDebugEnabled()){
            logger.debug("{} 销毁 ...", this.getClass().getName());
        }
    }

    /**
     * 在属性设置完毕后自动执行
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        genericInit();
    }
}
