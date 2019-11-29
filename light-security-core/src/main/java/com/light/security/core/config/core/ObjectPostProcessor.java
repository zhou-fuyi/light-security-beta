package com.light.security.core.config.core;

import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @InterfaceName ObjectPostProcessor
 * @Description 仿造SpringSecurity完成
 * Allows initialization of Objects. Typically this is used to call the {@link Aware}
 * methods, {@link InitializingBean#afterPropertiesSet()}, and ensure that
 * {@link DisposableBean#destroy()} has been invoked.
 *
 * @param <T> the bound of the types of Objects this {@link ObjectPostProcessor} supports.
 *
 *
 * 翻译:
 * 允许初始化对象。 通常，它用于调用{@link Aware}方法{@link InitializingBean＃afterPropertiesSet（）}，并确保已调用{@link DisposableBean＃destroy（）}。
 * @param <T> {@link ObjectPostProcessor}支持的对象类型的界限。
 *
 * 小声BB:
 * 骚气的后置处理器, 不要问, 因为我就是觉得它很骚气
 *
 * @Author ZhouJian
 * @Date 2019-11-29
 */
public interface ObjectPostProcessor<T> {

    /**
     * Initialize the object possibly returning a modified instance that should be used instead.
     *
     * 初始化对象可能会返回应使用的修改后的实例
     *
     * 可以使用该接口进行制定类型对象的后置处理
     * @param object
     * @param <O>
     * @return
     */
    <O extends T> O postProcess(O object);

}
