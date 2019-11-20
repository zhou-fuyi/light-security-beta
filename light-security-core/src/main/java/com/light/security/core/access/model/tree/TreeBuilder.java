package com.light.security.core.access.model.tree;

import com.light.security.core.exception.TreeBuilderException;

import java.util.Collection;
import java.util.List;

/**
 * @InterfaceName TreeBuilder
 * @Description 树构建器顶层接口
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface TreeBuilder<T> {

    /**
     * 用于构建树
     * @param trees
     * @return
     */
    List<T> build(Collection<? extends Tree> trees) throws TreeBuilderException;

    /**
     * 判断当前构建器是否支持
     * isAssignableFrom()方法是从类继承的角度去判断，instanceof关键字是从实例继承的角度去判断
     * isAssignableFrom()方法是判断是否为某个类的父类，instanceof关键字是判断是否某个类的子类
     * isAssignableFrom()方法的调用者和参数都是Class对象，调用者为父类，参数为本身或者其子类
     * instanceof关键字两个参数，前一个为类的实例，后一个为其本身或者父类的类型
     * @param target
     * @return
     */
    boolean support(Class<?> target);
}
