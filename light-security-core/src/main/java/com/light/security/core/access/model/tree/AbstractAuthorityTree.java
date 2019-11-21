package com.light.security.core.access.model.tree;

import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.model.tree.builder.AbstractAuthorityTreeBuilder;
import com.light.security.core.access.model.tree.builder.TreeBuilderConstant;
import com.light.security.core.access.model.tree.builder.manager.TreeBuilderManager;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
    private Collection<? extends AuthorityTree> children;
    private Collection<? extends GrantedAuthority> originAuthority;
    private TreeBuilderManager treeBuilderManager;

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
        this.originAuthority = builder.originAuthority;
        this.treeBuilderManager = builder.treeBuilderManager;
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

    public Collection<? extends AuthorityTree> getChildren() {
        return children;
    }

    public void setChildren(Collection<? extends AuthorityTree> children) {
        this.children = children;
    }

    public static abstract class Builder {

        private Integer id;
        private Collection<? extends GrantedAuthority> originAuthority;
        private TreeBuilderManager treeBuilderManager;

        private Integer parentId;
        private String code;
        private String name;
        private boolean enabled;
        private boolean open;
        Collection<? extends AuthorityTree> children = Collections.EMPTY_LIST;

        public Builder(Integer id, Collection<? extends GrantedAuthority> originAuthority, TreeBuilderManager treeBuilderManager){
            if (id == null || CollectionUtils.isEmpty(originAuthority) || treeBuilderManager == null){
                throw new IllegalArgumentException("构造器不接受空值参数 --> id is null or ( originAuthority is null or empty ) or treeBuilderManager is null");
            }
            this.id = id;
            this.originAuthority = Collections.unmodifiableCollection(originAuthority);
            this.treeBuilderManager = treeBuilderManager;
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

        public Builder children(Collection<? extends AuthorityTree> children){
            if (CollectionUtils.isEmpty(children)){
//                throw new IllegalArgumentException("传入孩子节点集合不能为空");
                children = Collections.EMPTY_LIST;
            }
            this.children = children;
            return this;
        }

        public abstract AbstractAuthorityTree build();

    }

    @Override
    public AuthorityTree loadRootNode() {
        if (this.parentId == TreeBuilderConstant.NULL_KEY)
            return this;
        return loadParentNode(this.parentId).loadRootNode();
    }

    @Override
    public AuthorityTree loadParentNode(Integer parentId) {
        parentId = parentId == null ? this.parentId : parentId;
        Integer finalParentId = parentId;
        if (finalParentId == null || finalParentId == TreeBuilderConstant.NULL_KEY){
            return null;
        }
        Collection<GrantedAuthority> authorityNodes = new ArrayList<>();
        this.originAuthority.forEach(authority -> {
            if (authority.getAuthorityId() == finalParentId){
                authorityNodes.add(authority);
                return;
            }
        });
        return (AuthorityTree) treeBuilderManager.singleBuild(authorityNodes.iterator().next(), this.originAuthority);
    }

    @Override
    public Collection<AuthorityTree> loadChildrenNode(Integer id) {
        if (id != null){
            Collection<GrantedAuthority> authorityNodes = new ArrayList<>();
            this.originAuthority.forEach(authority -> {
                if (authority.getAuthorityParentId() == id){
                    authorityNodes.add(authority);
                    return;
                }
            });
            return (Collection<AuthorityTree>) treeBuilderManager.doBuild(authorityNodes, this.originAuthority);
        }
        return (Collection<AuthorityTree>) this.children;
    }
}
