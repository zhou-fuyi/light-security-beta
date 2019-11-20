package com.light.security.core.access.model.tree;

import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * @ClassName AbstractAuthorityTree
 * @Description 权限树通用封装
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public abstract class AbstractAuthorityTree implements AuthorityTree {

    private Integer id;
    private Integer parentId;
    private String code;
    private String name;
    private boolean enabled;
    private boolean open;
    Collection<? extends AbstractAuthorityTree> children;

    public AbstractAuthorityTree() {
    }

    public AbstractAuthorityTree(Builder builder){
        this.id = builder.id;
        this.parentId = builder.parentId;
        this.code = builder.code;
        this.name = builder.name;
        this.enabled = builder.enabled;
        this.open = builder.open;
        this.children = builder.children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Collection<? extends AbstractAuthorityTree> getChildren() {
        return children;
    }

    public void setChildren(Collection<? extends AbstractAuthorityTree> children) {
        this.children = children;
    }

    public static abstract class Builder {

        private Integer id;

        private Integer parentId;
        private String code;
        private String name;
        private boolean enabled;
        private boolean open;
        Collection<? extends AbstractAuthorityTree> children = Collections.EMPTY_LIST;

        public Builder(Integer id){
            if (id == null){
                throw new IllegalArgumentException("构造器不接受空值参数 --> id is null");
            }
            this.id = id;
        }

        public Builder parentId(Integer parentId){
            this.parentId = parentId;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder enabled(boolean enabled){
            this.enabled = enabled;
            return this;
        }

        public Builder open(boolean open){
            this.open = open;
            return this;
        }

        public Builder children(Collection<? extends AbstractAuthorityTree> children){
            if (CollectionUtils.isEmpty(children)){
                throw new IllegalArgumentException("传入孩子节点集合不能为空");
            }
            this.children = children;
            return this;
        }

        public abstract AbstractAuthorityTree build();

    }

    @Override
    public AuthorityTree loadRootNode() {
        return null;
    }

    @Override
    public AuthorityTree loadParentNode(Integer parentId) {
        return null;
    }

    @Override
    public Collection<AuthorityTree> loadChildrenNode(Integer id) {
        return null;
    }
}
