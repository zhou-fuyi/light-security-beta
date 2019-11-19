package com.light.security.authentication;

/**
 * @InterfaceName CredentialsContainer
 * @Description 指示实现对象包含敏感数据, 可以将其删除使用{@code eraseCredentials}方法.
 * 仿照SpringSecurity中的<code>CredentialsContainer</code>
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface CredentialsContainer {

    /**
     * 擦除凭证
     */
    void eraseCredentials();

}
