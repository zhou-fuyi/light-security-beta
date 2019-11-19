package com.light.security.core.access.model.tree;

import com.light.security.core.exception.TreeBuilderException;

import java.util.Collection;

/**
 * @ClassName AuthorityTreeBuilder
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public abstract class AuthorityTreeBuilder implements TreeBuilder<AuthorityTree> {

    @Override
    public AuthorityTree builder(Collection<? extends Tree> trees) throws TreeBuilderException {
        return null;
    }

    @Override
    public boolean support(Class<?> target) {
        return false;
    }
}
