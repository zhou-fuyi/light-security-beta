package com.light.security.core.authentication;

import com.light.security.core.authentication.token.AnonymousAuthenticationToken;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.authentication.token.RememberMeAuthenticationToken;

/**
 * @ClassName AuthenticationTrustResolverImpl
 * @Description 仿照 SpringSecurity 完成
 * Basic implementation of {@link AuthenticationTrustResolver}.
 * <p>
 * Makes trust decisions based on whether the passed <code>Authentication</code> is an
 * instance of a defined class.
 * <p>
 * If {@link #anonymousClass} or {@link #rememberMeClass} is <code>null</code>, the
 * corresponding method will always return <code>false</code>.
 *
 *
 * 翻译:
 * {@link AuthenticationTrustResolver}的基本实现。
 * <p>
 * 根据所传递的<code> Authentication </ code>是否是已定义类的实例来做出信任决策。
 * <p>
 * 如果{@link #anonymousClass}或{@link #rememberMeClass}为<code> null </ code>，则相应的方法将始终返回<code> false </ code>。
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public class AuthenticationTrustResolverImpl implements AuthenticationTrustResolver{


    private Class<? extends Authentication> anonymousClass = AnonymousAuthenticationToken.class;
    private Class<? extends Authentication> rememberMeClass = RememberMeAuthenticationToken.class;

    /**
     * 注意可访问领域
     * @return
     */
    Class<? extends Authentication> getAnonymousClass() {
        return anonymousClass;
    }

    /**
     * 注意可访问领域
     * @return
     */
    Class<? extends Authentication> getRememberMeClass() {
        return rememberMeClass;
    }

    @Override
    public boolean isAnonymous(Authentication authentication) {
        if ((anonymousClass == null) || (authentication == null)){
            return false;
        }

        return anonymousClass.isAssignableFrom(authentication.getClass());
    }

    @Override
    public boolean isRememberMe(Authentication authentication) {
        if ((rememberMeClass == null) || (authentication == null)){
            return false;
        }
        return rememberMeClass.isAssignableFrom(authentication.getClass());
    }

    public void setAnonymousClass(Class<? extends Authentication> anonymousClass) {
        this.anonymousClass = anonymousClass;
    }

    public void setRememberMeClass(Class<? extends Authentication> rememberMeClass) {
        this.rememberMeClass = rememberMeClass;
    }
}
