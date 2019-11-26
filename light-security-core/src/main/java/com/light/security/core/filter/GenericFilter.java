package com.light.security.core.filter;

import com.light.security.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

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

    private static final String DEFAULT_PROCESS_URL = "/*";

    //Request匹配器
    private RequestMatcher matcher;

    protected volatile FilterConfig filterConfig;

    //当前过滤器作用的Url, 之前使用数组表示, 但是想着界限明确, 而且基本的Filter作用范围也很明确, 所以想着沿用SpringSecurity的套路
//    protected String processUrl;

    public GenericFilter(){
    }

    public GenericFilter(RequestMatcher matcher){
        Assert.notNull(matcher, "构造器不接受空值参数 --> matcher is null");
        this.matcher = matcher;
    }

    public void setMatcher(RequestMatcher matcher) {
        this.matcher = matcher;
    }

    public RequestMatcher getMatcher() {
        return matcher;
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

//    public String getProcessUrl() {
//        return processUrl;
//    }

//    public void setProcessUrl(String processUrl) {
//        if (StringUtils.isEmpty(processUrl)){
//            if (logger.isDebugEnabled()){
//                logger.debug("将会给当前过滤器: {} 指定默认的作用URL: {}", this.getClass().getSimpleName(), DEFAULT_PROCESS_URL);
//            }
//            processUrl = DEFAULT_PROCESS_URL;
//        }
//        this.processUrl = processUrl;
//    }

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
