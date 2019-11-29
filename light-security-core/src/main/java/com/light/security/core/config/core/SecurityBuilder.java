package com.light.security.core.config.core;

/**
 * @InterfaceName SecurityBuilder
 * @Description 安全构建器顶层接口 泛型 {@link T} 取名 于Target
 * @Author ZhouJian
 * @Date 2019-11-29
 *
 * @param <T> 表示需要被构建的对象类型
 */
public interface SecurityBuilder<T> {

    /**
     * 构建T类型对象, 可能返回null
     * @return
     * @throws Exception
     */
    T build() throws Exception;

}
