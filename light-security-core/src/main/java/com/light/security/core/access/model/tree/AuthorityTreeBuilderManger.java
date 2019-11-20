package com.light.security.core.access.model.tree;

import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.exception.LightSecurityException;
import com.light.security.core.exception.TreeBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

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

    public AuthorityTreeBuilderManger(List<AuthorityTreeBuilder> authorityTreeBuilders){
        Assert.notNull(authorityTreeBuilders, "treeBuilder集合不能为空");
        this.authorityTreeBuilders = Collections.unmodifiableList(authorityTreeBuilders);
    }

    /**
     * 根据传入权限集合的不同, 选择不同的权限树构建器进行树的构建
     * @param trees
     * @return
     */
    @Override
    public <T extends Tree> T doBuild(Collection<? extends Tree> trees){
        Assert.notEmpty(trees, "传入参数不能为空");
        List<GrantedAuthority> authorities = new ArrayList<>(trees.size());
        try {
            trees.forEach(item -> authorities.add((GrantedAuthority) item));
        }catch (ClassCastException castException){
            throw new IllegalArgumentException("传入权限不是GrantedAuthority类型, 该管理器暂时不支持");
        }
        if (additionCheck(new ArrayList<>(authorities))){
            throw new IllegalArgumentException("传入权限集合类型不一致，无法进行树的构建");
        }

        Class<? extends GrantedAuthority> toTest = authorities.iterator().next().getClass();
        LightSecurityException lastException = null;
        AuthorityTree authorityTree = null;
        for (AuthorityTreeBuilder builder : getAuthorityTreeBuilders()){
            if (!builder.support(toTest)){
                continue;
            }
            if (logger.isDebugEnabled()){
                logger.debug("传入权限集合类型为: {}, 使用 {} 进行树的构建", authorities.iterator().next().getAuthorityType(), builder.getClass().getName());
            }

            try {
                authorityTree = builder.build(authorities);
                if (authorityTree != null){
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
        if (authorityTree != null){
            return (T) authorityTree;
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
    private boolean additionCheck(List<GrantedAuthority> authorities){
        boolean flag = false;
        Map<String, List<GrantedAuthority>> temp = authorities.stream().collect(Collectors.groupingBy(GrantedAuthority::getAuthorityType));
        logger.info("temp is {}, temp`s size is {}", temp, temp.size());
        flag = temp.size() > 1;
        return flag;
    }

    public List<AuthorityTreeBuilder> getAuthorityTreeBuilders() {
        return authorityTreeBuilders;
    }

}
