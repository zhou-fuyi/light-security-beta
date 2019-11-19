package com.light.security.core.access.model.tree;

import com.light.security.core.exception.TreeBuilderException;

import java.util.Collection;

/**
 * @InterfaceName TreeBuilder
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface TreeBuilder<T> {

    /**
     * 用于构建树
     * @param trees
     * @return
     */
    T builder(Collection<? extends Tree> trees) throws TreeBuilderException;

    /**
     * 判断当前构建器是否支持
     * @param target
     * @return
     */
    boolean support(Class<?> target);
}
