package com.light.security.core.filter.chain;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @InterfaceName SecurityFilterChain
 * @Description 过滤器链接口
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public interface SecurityFilterChain {

    /**
     * 用于判断当前过滤器链是否匹配给定的请求
     * @param request
     * @return
     */
    boolean matches(HttpServletRequest request);

    /**
     * 获取当前过滤器链上的所有Filter的List集合
     * @return
     */
    List<Filter> getFilters();

}
