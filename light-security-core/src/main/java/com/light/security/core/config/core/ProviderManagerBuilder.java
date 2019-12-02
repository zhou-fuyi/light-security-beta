package com.light.security.core.config.core;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.AuthenticationManager;

/**
 * @InterfaceName ProviderManagerBuilder
 * @Description 用于构建 {@link AuthenticationManager}的实例{@link com.light.security.core.authentication.ProviderManager}
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public interface ProviderManagerBuilder<B extends ProviderManagerBuilder<B>> extends SecurityBuilder<AuthenticationManager> {

    /**
     * 根据传入的自定义{@link AuthenticationProvider}添加身份验证。
     * 由于{@link AuthenticationProvider}实现是未知的，因此所有自定义操作都必须在外部完成，
     * 并且{@link ProviderManagerBuilder}会立即返回。
     *
     * 请注意，如果在添加{@link AuthenticationProvider}时发生错误，则会引发Exception。
     * @param authenticationProvider
     * @return
     */
    B authenticationProvider(AuthenticationProvider authenticationProvider);
}
