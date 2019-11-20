package com.light.security.core.access.model;

import org.springframework.util.StringUtils;

/**
 * @ClassName MenuAuthority
 * @Description 菜单权限类
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public class MenuAuthority extends AbstractAuthority {

    private String link;
    private String icon;

    public MenuAuthority(){}

    public MenuAuthority(Builder builder) {
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

    public static class Builder extends AbstractAuthority.Builder{

        private String link;

        private String icon;

        public Builder(Integer authorityId, String code, String link) {
            super(authorityId, code);
            if (StringUtils.isEmpty(link)){
                throw new IllegalArgumentException("构造器不接受空值或null参数 --> link is null or '' ");
            }
            this.link = link;
        }

        public Builder icon(String icon){
            this.icon = icon;
            return this;
        }

        @Override
        public MenuAuthority build() {
            return new MenuAuthority(this);
        }
    }
}
