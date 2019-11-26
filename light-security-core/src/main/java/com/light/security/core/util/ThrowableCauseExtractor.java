package com.light.security.core.util;

/**
 * @InterfaceName ThrowableCauseExtractor
 * @Description 仿照SpringSecurity
 *Interface for handlers extracting the cause out of a specific {@link Throwable} type.
 *
 * 翻译:
 * 处理程序从特定的{@link Throwable}类型提取原因的接口
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public interface ThrowableCauseExtractor {

    /**
     * Extracts the cause from the provided <code>Throwable</code>.
     *
     * 从提供的<code> Throwable </ code>中提取原因
     * @param throwable
     * @return
     *
     * @throws IllegalArgumentException if <code>throwable</code> is <code>null</code> or otherwise considered invalid for the implementation
     *
     *                                  如果<code> throwable </ code>为<code> null </ code>，或者被认为对实现无效
     */
    Throwable extractCause(Throwable throwable);

}
