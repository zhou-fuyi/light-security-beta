package com.light.security.core.access.model.tree.builder;

import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.model.AbstractAuthority;
import com.light.security.core.access.model.Authority;
import com.light.security.core.access.model.tree.AuthorityTree;
import com.light.security.core.access.model.tree.Tree;
import com.light.security.core.access.model.tree.builder.manager.TreeBuilderManager;
import com.light.security.core.exception.TreeBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName AuthorityTreeBuilder
 * @Description 通用权限树构建器封装
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public abstract class AbstractAuthorityTreeBuilder implements AuthorityTreeBuilder {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Map<Integer, List<GrantedAuthority>> groupByParentIdMap;
    private TreeBuilderManager treeBuilderManager;

    public AbstractAuthorityTreeBuilder(){
        logger.info("启动权限构建器");
    }

//    protected AbstractAuthorityTreeBuilder(TreeBuilderManager treeBuilderManager){
//        Assert.notNull(treeBuilderManager, "构造器不接受空值参数 --> treeBuilderManager is null");
//        this.treeBuilderManager = treeBuilderManager;
//    }

    public void setTreeBuilderManager(TreeBuilderManager treeBuilderManager) {
        Assert.notNull(treeBuilderManager, "不接受空值参数 --> treeBuilderManager is null");
        this.treeBuilderManager = treeBuilderManager;
    }

    @Override
    public TreeBuilderManager getTreeBuilderManager() {
        return treeBuilderManager;
    }

    @Override
    public List<AuthorityTree> build(Collection<? extends Tree> root, Collection<? extends Tree> context) throws TreeBuilderException {
        synchronized (this.getClass()) {
            if (logger.isDebugEnabled()){
                logger.debug("开始进行权限树的构建");
            }

            Collection<GrantedAuthority> rootGrantedAuthorities = new ArrayList<>(root.size());
            Collection<GrantedAuthority> contextGrantedAuthorities = new ArrayList<>(context.size());
            try {
                if (!CollectionUtils.isEmpty(root)){
                    root.forEach(item -> rootGrantedAuthorities.add((GrantedAuthority) item));
                }
                context.forEach(item -> {
                    GrantedAuthority grantedAuthority = (GrantedAuthority) item;
                    //处理权限父Id为null的情况
                    if (grantedAuthority.getAuthorityParentId() == null){
                        grantedAuthority.getAuthority().setAuthorityParentId(TreeBuilderConstant.NULL_KEY);
                    }
                    contextGrantedAuthorities.add(grantedAuthority);
                });
            }
//            catch (ClassCastException e){
//                throw new IllegalArgumentException("传入集合类型与GrantedAuthority不相符, 如存在自定义, 请完成Builder的自定义");
//            }
            catch (NullPointerException e){
                throw new IllegalArgumentException("传入集合中存在空值元素, 引发NPE问题 --> msg is : " + e.getMessage());
            }
            return doBuild(rootGrantedAuthorities, contextGrantedAuthorities);
        }
    }

    /**
     * 权限树的构建起点
     * @param context
     * @return
     * @throws TreeBuilderException
     */
    protected List<AuthorityTree> doBuild(Collection<? extends GrantedAuthority> root, Collection<? extends GrantedAuthority> context) throws TreeBuilderException {
        synchronized (this.groupByParentIdMap) {
            if (!CollectionUtils.isEmpty(root)){
                if (logger.isDebugEnabled()){
                    logger.debug("此处指定了树的根节点, 直接使用指定形式构建树");
                }
                List<AuthorityTree> result = new ArrayList<>(root.size());
                root.forEach(parent -> result.add(performBuild(parent, loadChildren(parent.getAuthorityId(), context), context)));
                return Collections.unmodifiableList(new ArrayList<>(result));
            }else {
                //默认构建树方式
                groupByParentIdMap = Collections.unmodifiableMap(context.stream().collect(Collectors.groupingBy(GrantedAuthority::getAuthorityParentId)));
                //取出所有parentId为null的节点作为父节点, 也是每棵树的根节点
                List<GrantedAuthority> nullParentIdGrantedAuthorityList = groupByParentIdMap.get(TreeBuilderConstant.NULL_KEY);
                if (CollectionUtils.isEmpty(nullParentIdGrantedAuthorityList)){
                    if (logger.isDebugEnabled()){
                        logger.debug("所给权限集合中, 不存在parentId = TreeBuilderConstant.NULL_KEY 的数据, 将返回空集合");
                    }
                    return Collections.emptyList();
                }
                List<AuthorityTree> result = new ArrayList<>(nullParentIdGrantedAuthorityList.size());
                //先构建父节点, 然后再构建子节点
                nullParentIdGrantedAuthorityList.forEach(parent -> result.add(performBuild(parent, loadChildren(parent.getAuthorityId(), context), context)));
                return Collections.unmodifiableList(new ArrayList<>(result));
            }
        }
    }


    /**
     * 由子类进行实现, 单个权限的树节点构建
     * 不同类型的权限, 构建过程也存在差异
     * @param target
     * @param children
     * @param originAuthorities
     * @return
     */
    protected abstract AuthorityTree performBuild(GrantedAuthority target, Collection<? extends AuthorityTree> children, Collection<? extends GrantedAuthority> originAuthorities);

    /**
     * 加载子节点
     * @param parentId
     * @param originAuthorities
     * @return
     */
    private List<AuthorityTree> loadChildren(Integer parentId, Collection<? extends GrantedAuthority> originAuthorities){
        if (groupByParentIdMap.get(parentId) == null){
            return Collections.emptyList();
        }
        List<AuthorityTree> authorityTrees = new ArrayList<>(groupByParentIdMap.get(parentId).size());
        groupByParentIdMap.get(parentId).forEach(menu -> {
            AuthorityTree authorityTree = performBuild(menu, loadChildren(menu.getAuthorityParentId(), originAuthorities), originAuthorities);
            authorityTrees.add(authorityTree);
        });
        return new ArrayList<>(authorityTrees);
    }

}
