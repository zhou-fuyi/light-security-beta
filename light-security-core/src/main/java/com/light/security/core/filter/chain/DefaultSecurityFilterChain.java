package com.light.security.core.filter.chain;

import com.light.security.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName DefaultSecurityFilterChain
 * @Description {@link SecurityFilterChain}的默认实现
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class DefaultSecurityFilterChain implements SecurityFilterChain {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 用于比对当前过滤器链是否匹配给定请求的匹配器
     */
    private final RequestMatcher requestMatcher;
    /**
     * 用于存储当前过滤器链的过滤器
     */
    private final List<Filter> filters;

    public DefaultSecurityFilterChain(RequestMatcher requestMatcher, Filter... filters){
        this(requestMatcher, Arrays.asList(filters));
    }

    public DefaultSecurityFilterChain(RequestMatcher requestMatcher, List<Filter> filters){
        logger.info("Creating filter chain: " + requestMatcher + ", " + filters);
        this.requestMatcher = requestMatcher;
        this.filters = new ArrayList<Filter>(filters);
    }

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    @Override
    public List<Filter> getFilters() {
        return this.filters;
    }
}
