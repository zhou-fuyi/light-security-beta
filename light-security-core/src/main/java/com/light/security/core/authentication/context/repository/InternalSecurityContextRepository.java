package com.light.security.core.authentication.context.repository;

import com.light.security.core.authentication.context.DefaultSecurityContext;
import com.light.security.core.authentication.context.SecurityContext;
import com.light.security.core.authentication.context.holder.HttpRequestResponseHolder;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.subject.Subject;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.cache.holder.AuthenticatedContextCacheHolder;
import com.light.security.core.cache.model.InternalExpiredValueWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName InternalSecurityContextRepository
 * @Description <code>SecurityContextRepository</code>的一种实现(使用内部自实现缓存)，用于在请求之间存储<code>SecurityContext</code>
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class InternalSecurityContextRepository implements SecurityContextRepository, InitializingBean {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    private static final String DEFAULT_TOKEN_FIELD_NAME = "_auth";

    private String tokenKey = DEFAULT_TOKEN_FIELD_NAME;
    //当前线程内Authentication的共享数据
    private SecurityContextHolder securityContextHolder;
    //认证完成后主体数据存储
    private AuthenticatedContextCacheHolder authenticatedContextCacheHolder;

    public InternalSecurityContextRepository(){}

    public SecurityContextHolder getSecurityContextHolder() {
        return securityContextHolder;
    }

    public void setSecurityContextHolder(SecurityContextHolder securityContextHolder) {
        this.securityContextHolder = securityContextHolder;
    }

    public AuthenticatedContextCacheHolder getAuthenticatedContextCacheHolder() {
        return authenticatedContextCacheHolder;
    }

    public void setAuthenticatedContextCacheHolder(AuthenticatedContextCacheHolder authenticatedContextCacheHolder) {
        this.authenticatedContextCacheHolder = authenticatedContextCacheHolder;
    }

    private SecurityContext readSecurityContext(HttpServletRequest request) {
        final boolean debug = logger.isDebugEnabled();

        final String auth_header = request.getHeader(tokenKey);
        if (StringUtils.isEmpty(auth_header)){
            if (debug){
                logger.debug("当前请求的Header中未设置token: _auth, _auth`s value is null or '' ");
            }
            return null;
        }

        Authentication authentication = null;
        try {
            authentication = authenticatedContextCacheHolder.get(auth_header).getContent();
        }catch (NullPointerException e){
            if (debug){
                logger.debug("当前token对应账户已过期");
            }
            return null;
        }
        SecurityContext context = new DefaultSecurityContext(authentication);
        if (null == context){
            if (debug){
                logger.debug("SupportExpiredAuthenticatedContextCache中key is {} 的 InternalExpiredValueWrapper 的 context属性为null", auth_header);
            }
        }
        if (debug){
            logger.debug("从 SupportExpiredAuthenticatedContextCache 取得有效的数据: {}, 当前key is {}", context, auth_header);
        }
        return context;
    }

    private SecurityContext genericNewContext() {
        return securityContextHolder.createEmptyContent();
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        SecurityContext context = readSecurityContext(request);
        if (null == context){
            if (logger.isDebugEnabled()){
                logger.debug("没有从SupportExpiredAuthenticatedContextCache中获取到有效的SecurityContext, 将会创建一个空的SecurityContext实现.");
            }
            context = genericNewContext();
        }
        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        final Authentication authentication = context == null ? null : context.getAuthentication();
        if (null == authentication){
            if (logger.isDebugEnabled()){
                logger.debug("SecurityContext是一个空的实现, 不会使用 SupportExpiredAuthenticatedContextCache 进行存储");
            }
            return;
        }
        String key = request.getHeader(tokenKey);
        if (StringUtils.isEmpty(key)){
            key = authentication.getAuth() != null ? authentication.getAuth().toString() : null;
        }

        if (StringUtils.isEmpty(key)){
            if (logger.isWarnEnabled()){
                logger.warn("_auth 值无故丢失, 请检查程序安全性");
            }
        }else {
            /**
             * 这里进行重新放入是需要存在：当前线程过程中进行了必要性、业务性的Authentication的数据修改, 保持数据的一致性
             * {@link com.light.security.core.cache.context.concurrent.SupportExpiredAuthenticatedContextCache}中进行了重复插入的控制
             * 这里需要考虑是否更新过期时间达到续签的效果, 暂时先不更改过期时间
             */
            authenticatedContextCacheHolder.put(key, new InternalExpiredValueWrapper<Authentication>(key, authentication));
            if (logger.isDebugEnabled()){
                logger.debug("SecurityContext: {}, 已经使用 SupportExpiredAuthenticatedContextCache 进行存储", authentication);
            }
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(securityContextHolder, "SecurityContextHolder 不能为null");
        Assert.notNull(authenticatedContextCacheHolder, "AuthenticatedContextCacheHolder 不能为null");
    }
}
