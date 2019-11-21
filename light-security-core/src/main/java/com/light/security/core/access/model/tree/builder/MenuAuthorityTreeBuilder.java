package com.light.security.core.access.model.tree.builder;

import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.model.MenuAuthority;
import com.light.security.core.access.model.tree.AuthorityTree;
import com.light.security.core.access.model.tree.MenuAuthorityTree;

import java.util.*;

/**
 * @ClassName MenuAuthorityTreeBuilder
 * @Description 菜单权限树构建器
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public class MenuAuthorityTreeBuilder extends AbstractAuthorityTreeBuilder {

//    public MenuAuthorityTreeBuilder(TreeBuilderManager treeBuilderManager) {
//        super(treeBuilderManager);
//    }

    @Override
    protected AuthorityTree performBuild(GrantedAuthority target, Collection<? extends AuthorityTree> children, Collection<? extends GrantedAuthority> originAuthorities) {
        MenuAuthority menu = (MenuAuthority) target.getAuthority();
        AuthorityTree menuTree = new MenuAuthorityTree.Builder(menu.getId(), menu.getLink(), originAuthorities, getTreeBuilderManager())
                .icon(menu.getIcon())
                .parentId(menu.getParentId())
                .name(menu.getName())
                .code(menu.getCode())
                .enabled(menu.isEnabled())
                .open(menu.isOpen())
                .children(children)
                .build();
        return menuTree;
    }



    @Override
    public boolean support(Class<?> target) {
        return MenuAuthority.class.isAssignableFrom(target);
    }
}
