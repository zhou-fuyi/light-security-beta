package com.light.security.core.access.model.tree.builder.manager;

import com.light.security.core.access.model.tree.Tree;

import java.util.Collection;

/**
 * @InterfaceName TreeBuilderManager
 * @Description 树构建管理器顶层接口
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public interface TreeBuilderManager {

    Collection<? extends Tree> doBuild(Collection<? extends Tree> root, Collection<? extends Tree> context);

    Tree singleBuild(Tree tree, Collection<? extends Tree> context);

}
