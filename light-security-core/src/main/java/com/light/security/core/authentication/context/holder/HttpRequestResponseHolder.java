package com.light.security.core.authentication.context.holder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName HttpRequestResponseHolder
 * @Description 封装了<code>HTTPServletRequest</code>和<code>HTTPServletResponse</code>, 参考SpringSecurity 中的 <code>HttpRequestResponseHolder</code>
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class HttpRequestResponseHolder {

    private HttpServletRequest request;
    private HttpServletResponse response;

    public HttpRequestResponseHolder(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
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
