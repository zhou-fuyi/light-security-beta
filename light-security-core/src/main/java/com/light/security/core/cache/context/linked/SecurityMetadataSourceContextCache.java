package com.light.security.core.cache.context.linked;

import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.util.matcher.RequestMatcher;

import java.util.Collection;

/**
 * @ClassName SecurityMetadataSourceContextCache
 * @Description 用于存储SecurityMetadataSource, 内部使用{@link java.util.LinkedHashMap}实现, 保持数据插入顺序
 * , 便于在权限数据的最后放入一个{@link com.light.security.core.util.matcher.AnyRequestMatcher}, 这样能保证即使
 * 当前请求不能常规的匹配到对应数据, 也不至于返回null
 * @Author ZhouJian
 * @Date 2019-12-14
 */
public class SecurityMetadataSourceContextCache extends AbstractLinkedContextCache<RequestMatcher, Collection<ConfigAttribute>>{
}
