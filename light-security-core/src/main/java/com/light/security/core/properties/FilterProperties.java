package com.light.security.core.properties;

/**
 * @ClassName FilterProperties
 * @Description 过滤器配置类
 * @Author ZhouJian
 * @Date 2019-11-18
 */
public class FilterProperties {

    private DelegatingFilterProxyProperties proxy = new DelegatingFilterProxyProperties();

    /**
     * 门户预处理过滤器配置
     */
    private PretreatmentFilterProperties pretreatment = new PretreatmentFilterProperties();

    /**
     * 认证过滤器配置
     */
    private AuthenticationFilterProperties authentication = new AuthenticationFilterProperties();


    /**
     * 异常转换过滤器配置
     */
    private ExceptionTranslationFilterProperties exceptionTranslation = new ExceptionTranslationFilterProperties();

    /**
     * 授权过滤器配置
     */
    private AuthorizationFilterProperties authorization = new AuthorizationFilterProperties();

    public FilterProperties() {
    }

    public DelegatingFilterProxyProperties getProxy() {
        return proxy;
    }

    public void setProxy(DelegatingFilterProxyProperties proxy) {
        this.proxy = proxy;
    }

    public PretreatmentFilterProperties getPretreatment() {
        return pretreatment;
    }

    public void setPretreatment(PretreatmentFilterProperties pretreatment) {
        this.pretreatment = pretreatment;
    }

    public AuthenticationFilterProperties getAuthentication() {
        return authentication;
    }

    public void setAuthentication(AuthenticationFilterProperties authentication) {
        this.authentication = authentication;
    }

    public ExceptionTranslationFilterProperties getExceptionTranslation() {
        return exceptionTranslation;
    }

    public void setExceptionTranslation(ExceptionTranslationFilterProperties exceptionTranslation) {
        this.exceptionTranslation = exceptionTranslation;
    }

    public AuthorizationFilterProperties getAuthorization() {
        return authorization;
    }

    public void setAuthorization(AuthorizationFilterProperties authorization) {
        this.authorization = authorization;
    }

}
