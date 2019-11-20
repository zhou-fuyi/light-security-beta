package com.light.security.core.access.model;

/**
 * @ClassName ELementAuthority
 * @Description 元素权限类
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public class ElementAuthority extends AbstractAuthority{

    public ElementAuthority(){}

    public ElementAuthority(Builder builder){
        super(builder);
    }

    public static class Builder extends AbstractAuthority.Builder {

        public Builder(Integer authorityId, String code) {
            super(authorityId, code);
        }

        @Override
        public ElementAuthority build() {
            return new ElementAuthority(this);
        }
    }
}
