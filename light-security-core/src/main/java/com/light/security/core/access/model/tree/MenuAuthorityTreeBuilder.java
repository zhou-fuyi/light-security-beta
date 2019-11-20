package com.light.security.core.access.model.tree;

import com.light.security.core.access.model.MenuAuthority;
import com.light.security.core.exception.TreeBuilderException;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName MenuAuthorityTreeBuilder
 * @Description 菜单权限树构建器
 * @Author ZhouJian
 * @Date 2019-11-20
 */
public class MenuAuthorityTreeBuilder extends AbstractAuthorityTreeBuilder {

    // TODO: 2019-11-20 待完成 
    @Override
    protected List<AuthorityTree> doBuild(Collection<? extends Tree> authorities) throws TreeBuilderException {
        return null;
    }

    @Override
    public boolean support(Class<?> target) {
        return MenuAuthority.class.isAssignableFrom(target);
    }
}
