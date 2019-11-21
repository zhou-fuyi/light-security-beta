package com.light.security.core.access.authority;

import com.light.security.core.access.model.Authority;
import com.light.security.core.access.model.tree.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * @ClassName AbstractGrantedAuthority
 * @Description 已授予权限通用实现
 * @Author ZhouJian
 * @Date 2019-11-21
 */
public abstract class AbstractGrantedAuthority implements GrantedAuthority {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Authority authority;

    protected AbstractGrantedAuthority(Authority authority){
        Assert.notNull(authority, "构造器不接受空值参数 --> authority is null");
        this.authority = authority;
    }

    @Override
    public Authority getAuthority() {
        return this.authority;
    }

    @Override
    public String getAuthorityType() {
        return this.authority.getAuthorityType();
    }

    @Override
    public Integer getAuthorityParentId() {
        return authority.getAuthorityParentId();
    }

    @Override
    public Integer getAuthorityId() {
        return authority.getAuthorityId();
    }

    @Override
    public Tree loadRootNode() {
        if (logger.isDebugEnabled()){
            logger.debug("提供空实现, 该方法请使用AuthorityTree进行调用");
        }
        return null;
    }

    @Override
    public Tree loadParentNode(Integer parentId) {
        if (logger.isDebugEnabled()){
            logger.debug("提供空实现, 该方法请使用AuthorityTree进行调用");
        }
        return null;
    }

    @Override
    public Collection<? extends Tree> loadChildrenNode(Integer id) {
        if (logger.isDebugEnabled()){
            logger.debug("提供空实现, 该方法请使用AuthorityTree进行调用");
        }
        return null;
    }
}
