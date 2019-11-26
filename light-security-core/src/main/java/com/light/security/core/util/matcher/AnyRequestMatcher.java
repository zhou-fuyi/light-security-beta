package com.light.security.core.util.matcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName AnyRequestMatcher
 * @Description 可以匹配任意请求
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public final class AnyRequestMatcher extends AbstractRequestMatcher {

    public static final AnyRequestMatcher INSTANCE = new AnyRequestMatcher();

    /**
     * always return true
     * @param request
     * @return
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        return true;
    }
}
