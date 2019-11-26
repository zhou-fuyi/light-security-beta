package com.light.security.core.util.matcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @InterfaceName RequestMatcher
 * @Description 仿照SpringSecurity
 * Simple strategy to match an <tt>HttpServletRequest</tt>
 *
 * 翻译:
 * 简单的 HttpServletRequest 匹配策略
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public interface RequestMatcher {

    /**
     * Decides whether the rule implemented by the strategy matches the supplied request.
     * 具体的匹配策略规则
     * @param request
     * @return
     */
    boolean matches(HttpServletRequest request);
}
