package com.light.security.core.authentication.dao;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.subject.DefaultPostAuthenticationChecker;
import com.light.security.core.authentication.subject.DefaultPreAuthenticationChecker;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.authentication.subject.SubjectDetailChecker;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.authentication.token.UsernamePasswordAuthenticationToken;
import com.light.security.core.cache.holder.AuthenticatedContextCacheHolder;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.SubjectNameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * @ClassName AbstractSubjectDetailAuthenticationProvider
 * @Description {@link DaoAuthenticationProvider}的抽象实现
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public abstract class AbstractSubjectDetailAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    private AuthenticatedContextCacheHolder authenticatedContextCacheHolder;
    private SubjectDetailChecker preAuthenticationChecker = new DefaultPreAuthenticationChecker();
    private SubjectDetailChecker postAuthenticationChecker = new DefaultPostAuthenticationChecker();

    /**
     * 由子类实现并定义自己的检查逻辑, 通常这里都会进行密码校验
     * 该检测程序在{@link DefaultPreAuthenticationChecker#check(SubjectDetail)}后面,
     * 在{@link DefaultPostAuthenticationChecker#check(SubjectDetail)}前面
     * @param detail
     * @param authenticationToken
     * @throws AuthenticationException
     */
    protected abstract void additionalAuthenticationChecks(SubjectDetail detail, UsernamePasswordAuthenticationToken authenticationToken) throws AuthenticationException;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, "该程序只支持 UsernamePasswordAuthenticationToken 类型");
        String username = (authentication.getSubject() == null) ? "NONE_PROVIDER" : authentication.getName();
        boolean cacheWasUsed = true;
        SubjectDetail subject =  getSubjectFormCache(username);

        if (subject == null){
            cacheWasUsed = false;

            try {
                subject = retrieveSubject(username, (UsernamePasswordAuthenticationToken) authentication);
            }catch (SubjectNameNotFoundException e){
                logger.debug("User '" + username + "' not found");
                throw e;
            }
        }
        Assert.notNull(subject, "retrieveSubject 返回账户数据为null, 不符合接口规定");

        try {
            preAuthenticationChecker.check(subject);
            additionalAuthenticationChecks(subject, (UsernamePasswordAuthenticationToken) authentication);
        }catch (AuthenticationException ex){
            if (cacheWasUsed){
                cacheWasUsed = false;
                subject = retrieveSubject(username, (UsernamePasswordAuthenticationToken) authentication);
                preAuthenticationChecker.check(subject);
                additionalAuthenticationChecks(subject, (UsernamePasswordAuthenticationToken) authentication);
            }else {
                throw ex;
            }
        }

        postAuthenticationChecker.check(subject);

        if (!cacheWasUsed){
            // TODO: 2019-12-02 未完成数据的缓存实现, 未定义缓存中key值选取
            // 存放检索到的账户数据
//            this.authenticatedContextCacheHolder.put(username, new InternalExpiredValueWrapper<Authentication>());
            // 存放检索到的账户数据的权限数据
        }

        Object toReturn = subject;
        return createSuccessAuthentication(toReturn, authentication, subject);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(authenticatedContextCacheHolder, "AuthenticatedContextCacheHolder 不能为null");
        doAfterPropertiesSet();
    }

    protected abstract void doAfterPropertiesSet();

    protected Authentication createSuccessAuthentication(Object subject, Authentication authentication, SubjectDetail subjectDetail){
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(subject, authentication.getCredentials(), subjectDetail.getRoles());
        result.setDetails(authentication.getDetails());
        return result;
    }

    /**
     * 检索账户
     * @param subjectName
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    protected abstract SubjectDetail retrieveSubject(String subjectName, UsernamePasswordAuthenticationToken authenticationToken) throws AuthenticationException;

    public AuthenticatedContextCacheHolder getAuthenticatedContextCacheHolder() {
        return authenticatedContextCacheHolder;
    }

    public void setAuthenticatedContextCacheHolder(AuthenticatedContextCacheHolder authenticatedContextCacheHolder) {
        this.authenticatedContextCacheHolder = authenticatedContextCacheHolder;
    }


    private SubjectDetail getSubjectFormCache(String key){
        Authentication authentication = null;
        try {
            authentication = authenticatedContextCacheHolder.get(key).getContent();
            if (authentication == null) {
                return null;
            }
        }catch (NullPointerException e){
            if (logger.isDebugEnabled()){
                logger.debug("缓存中不存在 key为: {} 的账户", key);
            }
            return null;
        }

        return (SubjectDetail) authentication.getSubject();
    }
}
