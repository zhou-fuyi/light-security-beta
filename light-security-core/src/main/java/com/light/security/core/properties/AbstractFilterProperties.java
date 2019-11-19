package com.light.security.core.properties;


/**
 * @ClassName AbstractFilterProperties
 * @Description 用于抽离过滤器的公共属性
 * @Author ZhouJian
 * @Date 2019-11-18
 */
public abstract class AbstractFilterProperties {

    protected String[] processUrl;

    protected AbstractFilterProperties(){}

    public String[] getProcessUrl() {
        return processUrl;
    }

    public void setProcessUrl(String[] processUrl) {
        this.processUrl = processUrl;
    }
}
