package com.light.security.core.access.model.tree;

import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.model.tree.builder.manager.TreeBuilderManager;

import java.util.Collection;

/**
 * @ClassName ElementAuthorityTree
 * @Description 元素权限菜单数
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public class ElementAuthorityTree extends AbstractAuthorityTree {

    public ElementAuthorityTree(){}

    public ElementAuthorityTree(Builder builder) {
        super(builder);
    }

    public static class Builder extends AbstractAuthorityTree.Builder {

        public Builder(Integer id, Collection<? extends GrantedAuthority> originAuthority, TreeBuilderManager treeBuilderManager) {
            super(id, originAuthority, treeBuilderManager);
        }

        @Override
        public ElementAuthorityTree build() {
            return new ElementAuthorityTree(this);
        }
    }
}
