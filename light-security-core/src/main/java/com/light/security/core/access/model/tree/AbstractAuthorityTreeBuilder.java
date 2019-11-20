package com.light.security.core.access.model.tree;

import com.light.security.core.exception.TreeBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName AuthorityTreeBuilder
 * @Description 通用权限树构建器封装
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public abstract class AbstractAuthorityTreeBuilder implements AuthorityTreeBuilder {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<AuthorityTree> build(Collection<? extends Tree> trees) throws TreeBuilderException {
        if (logger.isDebugEnabled()){
            logger.debug("开始进行权限树的构建");
        }
        return doBuild(trees);
    }

    /**
     * 由子类实现, 不同类型的权限, 构建过程也存在差异
     * @param authorities
     * @return
     * @throws TreeBuilderException
     */
    protected abstract List<AuthorityTree> doBuild(Collection<? extends Tree> authorities) throws TreeBuilderException;

}
