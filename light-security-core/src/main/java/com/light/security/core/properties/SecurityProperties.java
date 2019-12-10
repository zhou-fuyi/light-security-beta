package com.light.security.core.properties;

import com.light.security.core.constant.AuthTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @ClassName SecurityProperties
 * @Description 系统配置类, 用于将配置文件转换为对应的类对象
 * @Author ZhouJian
 * @Date 2019-11-18
 */
@ConfigurationProperties(prefix = "light.security")
public class SecurityProperties {

    private String name;

    /**
     * 默认使用简易模式
     */
    private AuthTypeEnum authType = AuthTypeEnum.SIMPLE;

    /**
     * 过滤器配置
     */
    private FilterProperties filter = new FilterProperties();

    /**
     * 缓存配置器
     */
    private CacheProperties cache = new CacheProperties();

    private List<String> ignored = new ArrayList<>();

    public SecurityProperties() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuthTypeEnum getAuthType() {
        return authType;
    }

    public void setAuthType(AuthTypeEnum authType) {
        this.authType = authType;
    }

    public FilterProperties getFilter() {
        return filter;
    }

    public void setFilter(FilterProperties filter) {
        this.filter = filter;
    }

    public CacheProperties getCache() {
        return cache;
    }

    public void setCache(CacheProperties cache) {
        this.cache = cache;
    }

    public List<String> getIgnored() {
        return ignored;
    }

    public void setIgnored(List<String> ignored) {
        this.ignored = ignored;
    }
}
