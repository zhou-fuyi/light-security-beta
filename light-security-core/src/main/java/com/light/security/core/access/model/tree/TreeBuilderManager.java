package com.light.security.core.access.model.tree;

import java.util.Collection;

/**
 * @InterfaceName TreeBuilderManager
 * @Description 树构建管理器顶层接口
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public interface TreeBuilderManager {

    <T extends Tree> T doBuild(Collection<? extends Tree> trees);

}
