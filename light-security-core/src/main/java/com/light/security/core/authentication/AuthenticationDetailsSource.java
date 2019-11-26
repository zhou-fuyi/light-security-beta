package com.light.security.core.authentication;

/**
 * @InterfaceName AuthenticationDetailsSource
 * @Description 仿照SpringSecurity
 * Provides a {@link com.light.security.core.authentication.token.Authentication#getDetails()} object for a given web request.
 * 为上述方法提供一个数据对象, 其中包含了当次 web 请求
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public interface AuthenticationDetailsSource<C, T> {

    /**
     *Called by a class when it wishes a new authentication details instance to be created.
     * 当类希望创建新的身份验证详细信息实例时由类调用
     * @param context
     * @return
     */
    T buildDetails(C context);

}
