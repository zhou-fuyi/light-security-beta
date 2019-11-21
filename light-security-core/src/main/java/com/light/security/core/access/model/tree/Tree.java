package com.light.security.core.access.model.tree;

import java.util.Collection;

/**
 * @InterfaceName Tree
 * @Description 树的顶层接口
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface Tree {

    /**
     * 获取根节点
     * @return
     */
    Tree loadRootNode();

    /**
     * 根据父节点Id查询父节点
     * 如果传入parentId为null, 则使用当前树的parentId查询
     * @param parentId
     * @return
     */
    Tree loadParentNode(Integer parentId);

    /**
     * 根据指定节点的Id查找所有的孩子节点（遍历）
     * 如果传入id为null, 则使用当前树的id查询
     * @param id
     * @return
     */
    Collection<? extends Tree> loadChildrenNode(Integer id);

}
