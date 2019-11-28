package com.light.security.core.access.meta;

/**
 * @InterfaceName FilterInvocationSecurityMetadataSource
 * @Description 仿照SpringSecurity完成
 * Marker interface for <code>SecurityMetadataSource</code> implementations that are
 * designed to perform lookups keyed on {@link com.light.security.core.access.FilterInvocation}s.
 *
 *
 * 翻译:
 *用于<code> SecurityMetadataSource </ code>实现的标记接口，这些实现旨在
 * 执行在{@link com.light.security.core.access.FilterInvocation}上键入的查找
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public interface FilterInvocationSecurityMetadataSource extends SecurityMetadataSource {
}
