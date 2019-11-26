package com.light.security.core.authentication;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @ClassName WebAuthenticationDetails
 * @Description 用于存储请求的一些详细信息
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public class WebAuthenticationDetails implements Serializable {

    private final String remoteAddress;
    private final String cacheId;

    public WebAuthenticationDetails(HttpServletRequest request){
        this(request, null);
    }

    public WebAuthenticationDetails(HttpServletRequest request, final String cacheId){
        this.remoteAddress = request.getRemoteAddr();
        this.cacheId = cacheId;
    }

    public WebAuthenticationDetails(final String remoteAddress, final String cacheId){
        this.remoteAddress = remoteAddress;
        this.cacheId = cacheId;
    }

    public String getCacheId() {
        return cacheId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }
}
