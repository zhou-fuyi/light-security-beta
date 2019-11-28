package com.light.security.core.access;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName FilterInvocation
 * @Description FilterInvocation
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class FilterInvocation {

    private FilterChain chain;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public FilterInvocation(ServletRequest request, ServletResponse response, FilterChain chain){
        if ((request == null) || (response == null) || (chain == null)){
            throw new IllegalArgumentException("构造器不接受空值参数 --> request is null or response is null or chain is null");
        }
        this.request = (HttpServletRequest) request;
        this.response = (HttpServletResponse) response;
        this.chain = chain;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public FilterChain getChain() {
        return chain;
    }

    public void setChain(FilterChain chain) {
        this.chain = chain;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
