package com.light.security.core.access.model.tree;

import com.light.security.core.access.role.GrantedAuthority;
import com.light.security.core.exception.LightSecurityException;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName TreeBuilderManger
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public class AuthorityTreeBuilderManger {

    private List<AuthorityTreeBuilder> authorityTreeBuilders;

    public AuthorityTreeBuilderManger(List<AuthorityTreeBuilder> authorityTreeBuilders){
        Assert.notNull(authorityTreeBuilders, "treeBuilder集合不能为空");
    }

    // TODO: 2019-11-19  
    public AuthorityTree build(Collection<GrantedAuthority> authorities){
        Assert.notEmpty(authorities, "传入参数不能为空");

        Class<? extends GrantedAuthority> toTest = authorities.iterator().next().getClass();
        for (AuthorityTreeBuilder builder : getAuthorityTreeBuilders()){
            if (!builder.support(toTest)){
                continue;
            }
            AuthorityTree authorityTree = null;

            try {
                authorityTree = builder.builder(authorities);
            }catch (LightSecurityException e){}
            return authorityTree;
        }
        return null;
    }

    public List<AuthorityTreeBuilder> getAuthorityTreeBuilders() {
        return authorityTreeBuilders;
    }
}
