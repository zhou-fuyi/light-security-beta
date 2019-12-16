package com.light.security.core.config.core;


import com.light.security.core.config.core.configurer.IgnoredResourcesConfigurerAdapter;

/**
 * @InterfaceName IgnoredResourcesConfigurer
 * @Description 用于配置需要忽略的资源的配置器
 * 可以实现该接口, 进行公共资源的自定义注册
 * @Author ZhouJian
 * @Date 2019-12-16
 */
public interface IgnoredResourcesConfigurer {

    /**
     * 这里可以进行一些资源的初始化, 也可以什么都不做
     * @throws Exception
     */
    void init() throws Exception;

    /**
     * 用于注册公共资源
     * @param ignoredResourceRegistry
     * @throws Exception
     */
    void ignoredRegistry(IgnoredResourcesConfigurerAdapter.IgnoredResourceRegistry ignoredResourceRegistry) throws Exception;

}
