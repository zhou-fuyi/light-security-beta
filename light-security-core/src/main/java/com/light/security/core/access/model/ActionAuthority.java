package com.light.security.core.access.model;

import org.springframework.util.StringUtils;

/**
 * @ClassName ActionAuthority
 * @Description API权限类
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public class ActionAuthority extends AbstractAuthority {

    private String method;
    private String pattern;

    public ActionAuthority(){
    }

    public ActionAuthority(Builder builder){
        super(builder);
        this.method = builder.method;
        this.pattern = builder.pattern;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public static class Builder extends AbstractAuthority.Builder{

        private String pattern;

        private String method;

        public Builder(Integer authorityId, String code, String pattern) {
            super(authorityId, code);
            if (StringUtils.isEmpty(pattern)){
                throw new IllegalArgumentException("构造器不接受空值参数 --> pattern is null or '' ");
            }
            this.pattern = pattern;
        }

        public Builder method(String method){
            this.method = method;
            return this;
        }

        @Override
        public ActionAuthority build() {
            return new ActionAuthority(this);
        }
    }


}
