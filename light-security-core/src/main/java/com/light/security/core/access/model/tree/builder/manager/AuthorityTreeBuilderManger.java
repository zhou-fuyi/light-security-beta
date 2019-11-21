package com.light.security.core.access.model.tree.builder.manager;

import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.model.Authority;
import com.light.security.core.access.model.tree.AuthorityTree;
import com.light.security.core.access.model.tree.Tree;
import com.light.security.core.access.model.tree.builder.AuthorityTreeBuilder;
import com.light.security.core.access.model.tree.builder.manager.TreeBuilderManager;
import com.light.security.core.exception.LightSecurityException;
import com.light.security.core.exception.TreeBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName TreeBuilderManger
 * @Description 权限树构建器管理中心, 用于根据传入的权限集合类型选择对应的权限树构建器进行构建
 * 构建当前实例时, 需要传入业务中需要使用的<code>TreeBuilder</code>实例
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class AuthorityTreeBuilderManger implements TreeBuilderManager {

    private List<AuthorityTreeBuilder> authorityTreeBuilders;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public AuthorityTreeBuilderManger(){}

    public void setAuthorityTreeBuilders(List<AuthorityTreeBuilder> authorityTreeBuilders) {
        Assert.notNull(authorityTreeBuilders, "treeBuilder集合不能为空");
        this.authorityTreeBuilders = Collections.unmodifiableList(authorityTreeBuilders);
    }

    @Override
    public AuthorityTree singleBuild(Tree tree, Collection<? extends Tree> context) {
        if (tree == null || CollectionUtils.isEmpty(context)){
            throw new IllegalArgumentException("传入参数不能为空");
        }
//        Collection<? extends Tree> root = Arrays.asList(tree);
        Collection<? extends AuthorityTree> result = doBuild(Arrays.asList(tree), context);
        return CollectionUtils.isEmpty(result) ? null : result.iterator().next();
    }

    /**
     * 根据传入权限集合的不同, 选择不同的权限树构建器进行树的构建
     * @param root 可指定树的根节点
     * @param context
     * @return
     */
    @Override
    public Collection<? extends AuthorityTree>  doBuild( Collection<? extends Tree> root, Collection<? extends Tree> context){
        Assert.notEmpty(context, "传入参数不能为空");
        Collection<GrantedAuthority> contextAuthorities = new ArrayList<>(context.size());
        Collection<GrantedAuthority> rootAuthorities = new ArrayList<>();
        try {
            Collection<GrantedAuthority> additionCheckAuthorities = new ArrayList<>(context.size() + (context.size() / 2));
            //检查root
            if (!CollectionUtils.isEmpty(root)){
                root.forEach(item -> rootAuthorities.add((GrantedAuthority) item));
                additionCheckAuthorities.addAll(rootAuthorities);
            }
            context.forEach(item -> contextAuthorities.add((GrantedAuthority) item));
            additionCheckAuthorities.addAll(contextAuthorities);
            //检查传入权限集合中元素类型是否相同
            if (additionCheck(new ArrayList<>(additionCheckAuthorities))){
                throw new IllegalArgumentException("传入权限集合类型不一致 (可能出现的情况为: 指定root中类型不一致, 指定context中类型不一致, root和context中的类型不一致), 无法进行树的构建");
            }
        }catch (ClassCastException castException){
            throw new IllegalArgumentException("传入权限不是GrantedAuthority类型, 该管理器暂时不支持");
        }

        //获取当前权限集合的元素中包含的具体权限的Class类型
        Class<? extends Authority> toTest = contextAuthorities.iterator().next().getAuthority().getClass();
        LightSecurityException lastException = null;
        Collection<? extends AuthorityTree> authorityTrees = null;
        for (AuthorityTreeBuilder builder : getAuthorityTreeBuilders()){
            if (!builder.support(toTest)){
                continue;
            }
            if (logger.isDebugEnabled()){
                logger.debug("传入权限集合类型为: {}, 使用 {} 进行树的构建", contextAuthorities.iterator().next().getAuthorityType(), builder.getClass().getName());
            }

            try {
                authorityTrees = builder.build(rootAuthorities, contextAuthorities);
                if (!CollectionUtils.isEmpty(authorityTrees)){
                    break;
                }
            }catch (TreeBuilderException e){
                logger.error("权限树构造出现异常: {}",e.getMessage());
                throw e;
            }catch (LightSecurityException e){
                logger.warn("未知异常: {}", e.getMessage());
                lastException = e;
            }
        }
        if (!CollectionUtils.isEmpty(authorityTrees)){
            return Collections.unmodifiableCollection(authorityTrees);
        }
        /**
         * 说明for循环中没有找到support支持的builder
         */
        if (lastException == null){
            lastException = new TreeBuilderException(500, "没有找到支持当前权限类型的TreeBuilder");
        }
        throw lastException;
    }

    // TODO: 2019/11/19 待完善
    /**
     * 进行传入权限集合的类型一致性检查 
     * @param authorities
     * @return
     */
    private boolean additionCheck(Collection<GrantedAuthority> authorities){
        boolean flag = false;
        //这里是将传入集合根据集合内元素的type进行分组, 如果分组情况大于1则表示其中的type不一致, 这里有一个前提条件便是集合不能为空且type必须有值
        Map<String, List<GrantedAuthority>> temp = null;
        try {
            temp = authorities.stream().collect(Collectors.groupingBy(GrantedAuthority::getAuthorityType));
        }catch (NullPointerException e){
            throw new IllegalArgumentException("传入权限集合中存在元素的type属性为null情况");
        }
        logger.info("temp is {}, temp`s size is {}", temp, temp.size());
        flag = temp.size() > 1;
        return flag;
    }

    public List<AuthorityTreeBuilder> getAuthorityTreeBuilders() {
        return authorityTreeBuilders;
    }

}
