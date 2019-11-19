package com.light.security.core.access.model.tree;

import com.light.security.core.access.role.GrantedAuthority;
import com.light.security.core.exception.LightSecurityException;
import com.light.security.core.exception.TreeBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName TreeBuilderManger
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class AuthorityTreeBuilderManger {

    private List<AuthorityTreeBuilder> authorityTreeBuilders;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public AuthorityTreeBuilderManger(List<AuthorityTreeBuilder> authorityTreeBuilders){
        Assert.notNull(authorityTreeBuilders, "treeBuilder集合不能为空");
    }

    // TODO: 2019-11-19  
    public AuthorityTree build(Collection<GrantedAuthority> authorities){
        Assert.notEmpty(authorities, "传入参数不能为空");
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
                authorityTree = builder.builder(authorities);
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
            return authorityTree;
        }
        /**
         * 说明for循环中没有找到support支持的builder
         */
        if (lastException == null){
            lastException = new TreeBuilderException(500, "没有找到支持当前权限类型的TreeBuilder");
        }
        throw lastException;
    }

    // TODO: 2019/11/19 带完善 
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
