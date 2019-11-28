package com.light.security.core.cache.context.concurrent;

import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.util.matcher.RequestMatcher;

import java.util.Collection;

/**
 * @ClassName SecurityMetadataSourceContextCache
 * @Description 用于存储SecurityMetadataSource
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class SecurityMetadataSourceContextCache extends AbstractConcurrentContextCache<RequestMatcher, Collection<ConfigAttribute>> {
}
