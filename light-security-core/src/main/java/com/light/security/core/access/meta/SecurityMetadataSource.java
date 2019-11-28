package com.light.security.core.access.meta;

import com.light.security.core.access.ConfigAttribute;

import java.util.Collection;

/**
 * @InterfaceName SecurityMetadataSource
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public interface SecurityMetadataSource {

    Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException;

    Collection<ConfigAttribute> getAllAttributes();

    boolean supports(Class<?> clazz);
}
