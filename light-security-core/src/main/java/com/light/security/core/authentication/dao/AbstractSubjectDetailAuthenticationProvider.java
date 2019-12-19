package com.light.security.core.authentication.dao;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.subject.*;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.authentication.token.SubjectNamePasswordAuthenticationToken;
import com.light.security.core.cache.holder.AuthenticatedContextCacheHolder;
import com.light.security.core.cache.model.InternalExpiredValueWrapper;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.InternalServiceException;
import com.light.security.core.exception.SubjectNameNotFoundException;
import com.light.security.core.util.signature.InternalDefaultSignature;
import com.light.security.core.util.signature.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @ClassName AbstractSubjectDetailAuthenticationProvider
 * @Description {@link DaoAuthenticationProvider}的抽象实现
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public abstract class AbstractSubjectDetailAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private AuthenticatedContextCacheHolder authenticatedContextCacheHolder;

    @Autowired
    private SecurityContextHolder securityContextHolder;
    private SubjectDetailChecker preAuthenticationChecker = new DefaultPreAuthenticationChecker();
    private SubjectDetailChecker postAuthenticationChecker = new DefaultPostAuthenticationChecker();
    /**
     * 默认签名实现, 不可以, 其实就是包装了一下UUID
     *
     */
    private Signature signature = new InternalDefaultSignature();

    /**
     * 由子类实现并定义自己的检查逻辑, 通常这里都会进行密码校验
     * 该检测程序在{@link DefaultPreAuthenticationChecker#check(SubjectDetail)}后面,
     * 在{@link DefaultPostAuthenticationChecker#check(SubjectDetail)}前面
     * @param detail
     * @param authenticationToken
     * @throws AuthenticationException
     */
    protected abstract void additionalAuthenticationChecks(SubjectDetail detail, SubjectNamePasswordAuthenticationToken authenticationToken) throws AuthenticationException;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(SubjectNamePasswordAuthenticationToken.class, authentication, "该程序只支持 UsernamePasswordAuthenticationToken 类型");
        String subjectName = (authentication.getSubject() == null) ? "NONE_PROVIDER" : authentication.getName();
        boolean cacheWasUsed = true;
        SubjectDetail subject =  getSubjectFormCache(subjectName);

        if (subject == null){
            cacheWasUsed = false;

            try {
                subject = retrieveSubject(subjectName, (SubjectNamePasswordAuthenticationToken) authentication);
            }catch (SubjectNameNotFoundException e){
                logger.debug("Subject '" + subjectName + "' not found");
                throw e;
            }
        }
        Assert.notNull(subject, "retrieveSubject 返回账户数据为null, 不符合接口规定");

        try {
            preAuthenticationChecker.check(subject);
            additionalAuthenticationChecks(subject, (SubjectNamePasswordAuthenticationToken) authentication);
        }catch (AuthenticationException ex){
            if (cacheWasUsed){
                cacheWasUsed = false;
                subject = retrieveSubject(subjectName, (SubjectNamePasswordAuthenticationToken) authentication);
                preAuthenticationChecker.check(subject);
                additionalAuthenticationChecks(subject, (SubjectNamePasswordAuthenticationToken) authentication);
            }else {
                throw ex;
            }
        }

        postAuthenticationChecker.check(subject);

        Object toReturn = subject;

        Authentication successAuthentication = createSuccessAuthentication(toReturn, authentication, subject);
        if (!cacheWasUsed){
            cacheAuthentication(successAuthentication);
        }

        return successAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (SubjectNamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        Assert.notNull(authenticatedContextCacheHolder, "AuthenticatedContextCacheHolder 不能为null");
        doAfterPropertiesSet();
    }

    /**
     * 缓存账户数据
     * @param authentication
     */
    protected void cacheAuthentication(Authentication authentication){
        try {
            ((Subject) authentication.getSubject()).eraseCredentials();
        } catch (ClassCastException castException){
            throw new InternalServiceException(500, "请重写该方法完成 SubjectDetails 类型对象的密码脱敏处理");
        }
        securityContextHolder.getContext().setAuthentication(authentication);
        authentication = (SubjectNamePasswordAuthenticationToken) authentication;
//        String key = signature.sign(authentication.getName());
//        String key = signature.sign(null);
        String key = signature.sign(null, authentication.getName());
        ((SubjectNamePasswordAuthenticationToken) authentication).setAuth(key);
        authenticatedContextCacheHolder.put(key, new InternalExpiredValueWrapper<Authentication>(key, authentication));
    }

    protected abstract void doAfterPropertiesSet();

    protected Authentication createSuccessAuthentication(Object subject, Authentication authentication, SubjectDetail subjectDetail){
        SubjectNamePasswordAuthenticationToken result = new SubjectNamePasswordAuthenticationToken(subject, authentication.getCredentials(), subjectDetail.getRoles());
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
    protected abstract SubjectDetail retrieveSubject(String subjectName, SubjectNamePasswordAuthenticationToken authenticationToken) throws AuthenticationException;

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
