package com.light.security.core.access;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AuthorityAttribute
 * @Description 权限属性, 主要用于记录API权限的能够唯一代表一则权限数据的编码
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public class AuthorityAttribute implements ConfigAttribute {

    private final String attr;

    public AuthorityAttribute(String authorityAttr){
        Assert.hasText(authorityAttr, "构造器不接受空值参数 --> authorityAttr is null or '' ");
        this.attr = authorityAttr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfigAttribute){
            ConfigAttribute attribute = (ConfigAttribute) obj;
            return this.attr.equals(attribute.getAttribute());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.attr.hashCode();
    }

    @Override
    public String getAttribute() {
        return this.attr;
    }

    /**
     * 批量创建
     * @param authorityAttrs
     * @return
     */
    public List<ConfigAttribute> createList(String... authorityAttrs){
        Assert.notNull(authorityAttrs, "authorityAttrs 不能为 null");
        List<ConfigAttribute> attributes = new ArrayList<>(authorityAttrs.length);
        for (String attr : authorityAttrs){
            attributes.add(new AuthorityAttribute(attr.trim()));
        }
        return attributes;
    }

    @Override
    public String toString() {
        return "权限编码: " + this.attr;
    }
}
