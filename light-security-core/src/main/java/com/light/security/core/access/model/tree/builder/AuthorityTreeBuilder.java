package com.light.security.core.access.model.tree.builder;

import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.model.tree.AuthorityTree;
import com.light.security.core.access.model.tree.Tree;
import com.light.security.core.exception.TreeBuilderException;

import java.util.Collection;
import java.util.List;

/**
 * @InterfaceName AuthorityTreeBuilder
 * @Description 权限树构建器顶层接口
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public interface AuthorityTreeBuilder extends TreeBuilder {

    @Override
    List<? extends AuthorityTree> build(Collection<? extends Tree> root, Collection<? extends Tree> context) throws TreeBuilderException;
}
