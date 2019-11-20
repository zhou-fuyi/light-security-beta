package com.light.security.core.access.model.tree;

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

        public Builder(Integer id) {
            super(id);
        }

        @Override
        public ElementAuthorityTree build() {
            return new ElementAuthorityTree(this);
        }
    }
}
