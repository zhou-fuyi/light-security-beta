package com.light.security.core.access.model.tree.builder;

import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.model.ElementAuthority;
import com.light.security.core.access.model.tree.AuthorityTree;
import com.light.security.core.access.model.tree.ElementAuthorityTree;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @ClassName ElementAuthorityTreeBuilder
 * @Description 元素权限树构建器
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public class ElementAuthorityTreeBuilder extends AbstractAuthorityTreeBuilder {

//    public ElementAuthorityTreeBuilder(TreeBuilderManager treeBuilderManager) {
//        super(treeBuilderManager);
//    }

    @Override
    protected AuthorityTree performBuild(GrantedAuthority target, Collection<? extends AuthorityTree> children, Collection<? extends GrantedAuthority> originAuthorities) {
        ElementAuthority element = (ElementAuthority) target.getAuthority();
        AuthorityTree elementTree = new ElementAuthorityTree.Builder(element.getId(), originAuthorities, getTreeBuilderManager())
                .parentId(element.getParentId())
                .name(element.getName())
                .code(element.getCode())
                .enabled(element.isEnabled())
                .open(element.isOpened())
                .children(children)
                .build();
        return elementTree;
    }

    @Override
    public boolean support(Class<?> target) {
        /**
         * isAssignableFrom()方法是从类继承的角度去判断，instanceof关键字是从实例继承的角度去判断
         * isAssignableFrom()方法是判断是否为某个类的父类，instanceof关键字是判断是否某个类的子类
         * isAssignableFrom()方法的调用者和参数都是Class对象，调用者为父类，参数为本身或者其子类
         * instanceof关键字两个参数，前一个为类的实例，后一个为其本身或者父类的类型
         */
        return ElementAuthority.class.isAssignableFrom(target);
    }
}
