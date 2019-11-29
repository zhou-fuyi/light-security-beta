package com.light.security.core.config.core;

/**
 * @InterfaceName SecurityConfigurer
 * @Description 配置器顶层接口
 * @Author ZhouJian
 * @Date 2019-11-29
 *
 * @param <T> 表示需要辅助构建的对象类型
 * @param <B> 表示用于构建{@link T}的构建器对象类型
 */
public interface SecurityConfigurer<T, B extends SecurityBuilder<T>> {

    /**
     * Initialize the {@link SecurityBuilder}. Here only shared state should be created
     * and modified, but not properties on the {@link SecurityBuilder} used for building
     * the object. This ensures that the {@link #configure(SecurityBuilder)} method uses
     * the correct shared objects when building.
     *
     *
     * 翻译:
     * 初始化{@link SecurityBuilder}。 在这里，仅应创建和修改共享状态，而不能在用于构建对象的
     * {@link SecurityBuilder}上创建和修改属性。 这样可以确保{@link #configure（SecurityBuilder）}
     * 方法在构建时使用正确的共享库。
     * @param builder
     * @throws Exception
     */
    void init(B builder) throws Exception;

    /**
     * Configure the {@link SecurityBuilder} by setting the necessary properties on the
     * {@link SecurityBuilder}.
     *
     *
     * 翻译:
     * 通过在{@link SecurityBuilder}上设置必要的属性来配置{@link SecurityBuilder}。
     * @param builder
     * @throws Exception
     */
    void configure(B builder) throws Exception;
}
