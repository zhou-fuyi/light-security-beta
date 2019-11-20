package com.light.security.core.access.model.tree;

import org.springframework.util.StringUtils;

/**
 * @ClassName MenuAuthorityTree
 * @Description 菜单权限树
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public class MenuAuthorityTree extends AbstractAuthorityTree {

    private String link;
    private String icon;

    public MenuAuthorityTree() {
    }

    public MenuAuthorityTree(Builder builder) {
        super(builder);
        this.link = builder.link;
        this.icon = builder.icon;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public static class Builder extends AbstractAuthorityTree.Builder {

        private String link;

        private String icon;

        public Builder(Integer id, String link) {
            super(id);
            if (StringUtils.isEmpty(link)){
                throw new IllegalArgumentException("构造器不接受空值参数 --> link is null or '' ");
            }
            this.link = link;
        }

        public Builder icon(String icon){
            this.icon = icon;
            return this;
        }

        @Override
        public MenuAuthorityTree build() {
            return new MenuAuthorityTree(this);
        }
    }
}
