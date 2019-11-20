package com.light.security.core.access.model.tree;

import java.util.Collection;

/**
 * @InterfaceName AuthorityTreeNode
 * @Description 权限模块树接口
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface AuthorityTree extends Tree{

    @Override
    AuthorityTree loadRootNode();

    @Override
    AuthorityTree loadParentNode(Integer parentId);

    @Override
    Collection<AuthorityTree> loadChildrenNode(Integer id);

}
