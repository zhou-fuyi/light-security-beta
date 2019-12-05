package com.light.security.core.properties;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.Set;

/**
 * @ClassName DelegatingFilterProxyProperties
 * @Description {@link org.springframework.web.filter.DelegatingFilterProxy}的配置类
 * @Author ZhouJian
 * @Date 2019-12-05
 */
public class DelegatingFilterProxyProperties {

    private static final int DEFAULT_PROXY_FILTER_ORDER = FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER - 100;

    private int proxyOrder = DEFAULT_PROXY_FILTER_ORDER;

    private Set<String> filterDispatcherTypes;

    public int getProxyOrder() {
        return proxyOrder;
    }

    public void setProxyOrder(int proxyOrder) {
        this.proxyOrder = proxyOrder;
    }

    public Set<String> getFilterDispatcherTypes() {
        return filterDispatcherTypes;
    }

    public void setFilterDispatcherTypes(Set<String> filterDispatcherTypes) {
        this.filterDispatcherTypes = filterDispatcherTypes;
    }
}
