package com.light.security.core.filter;

import com.light.security.core.access.handler.AccessDeniedHandler;
import com.light.security.core.access.handler.AccessDeniedHandlerImpl;
import com.light.security.core.authentication.AuthenticationTrustResolver;
import com.light.security.core.authentication.AuthenticationTrustResolverImpl;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.entrypoint.AuthenticationEntryPoint;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AccessDeniedException;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.InsufficientAuthenticationException;
import com.light.security.core.util.ThrowableAnalyzer;
import com.light.security.core.util.ThrowableCauseExtractor;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName ExceptionTranslationFilter
 * @Description 异常转换过滤器, 这里不进行异常的直接处理, 而是进行部分类型异常的捕获并输送给指定的处理器
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public class ExceptionTranslationFilter extends GenericFilter{

    public ExceptionTranslationFilter(){}

    private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();//访问控制异常处理器
    //用于分析一个Exception抛出的原因, 通过异常栈进行查找
    private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    //身份验证信任解析器默认实现, 主要进行token来源的评估
    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

   // Init by constructor

    //处理ExceptionTranslationFilter之后出现的AuthenticationException, 这更像是一个引导程序, 进行重新认证的引导程序
    private AuthenticationEntryPoint authenticationEntryPoint;

    private SecurityContextHolder securityContextHolder;

    // Init by constructor

    // TODO: 2019-11-27 待完成RequestCache
//    private RequestCache requestCache = new HttpSessionRequestCache();


    public ExceptionTranslationFilter(AuthenticationEntryPoint authenticationEntryPoint, SecurityContextHolder securityContextHolder){
        Assert.notNull(authenticationEntryPoint, "构造器不接受空值参数 --> authenticationEntryPoint is null");
//        Assert.notNull(requestCache, "requestCache cannot be null");
        Assert.notNull(securityContextHolder, "构造器不接受空值参数 --> securityContextHolder is null");
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.securityContextHolder = securityContextHolder;
    }

    // Methods ========================================================================================================

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(authenticationEntryPoint, "authenticationEntryPoint 不能为null");
        Assert.notNull(securityContextHolder, "securityContextHolder 不能为null");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        try {
            chain.doFilter(request, response);
            if (logger.isDebugEnabled()){
                logger.debug("过滤器链处理程序正常");
            }
        }catch (IOException ex){
            throw ex;
        }catch (Exception ex){
            //提取异常的原因链
            Throwable[] causeChain = throwableAnalyzer.determineCauseChain(ex);
            //尝试获取AuthenticationException类型数据
            RuntimeException target = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);
            if (target == null){
                //若AuthenticationException获取失败, 则尝试获取AccessDeniedException
                target = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
            }
            //如果成功获取到Exception(目前target要么是AuthenticationException, 要么是AccessDeniedException)信息, 则进行处理
            if (target != null){
                handleLightSecurityException(request, response, chain, target);
            }else {
                //Rethrow ServletExceptions and RuntimeExceptions as-is (照原样重新抛出ServletException和RuntimeException)
                if (ex instanceof ServletException){
                    throw (ServletException) ex;
                }else if (ex instanceof RuntimeException){
                    throw (RuntimeException) ex;
                }

                // Wrap other Exceptions. This shouldn't actually happen as we've already covered all the possibilities for doFilter
                // 包装其他异常. 这实际上不应该发生, 因为我们已经介绍了doFilter的所有可能性
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 处理目前拦截的两种异常, 要么是AuthenticationException, 要么是AccessDeniedException, 且两种类型逻辑互斥, 不应该该同时出现
     * @param request
     * @param response
     * @param chain
     * @param exception
     * @throws IOException
     * @throws ServletException
     */
    private void handleLightSecurityException(HttpServletRequest request, HttpServletResponse response, FilterChain chain, RuntimeException exception) throws IOException, ServletException {
        //如果是AuthenticationException类型的异常
        if (exception instanceof AuthenticationException){
            logger.debug("Authentication exception occurred; redirecting to authentication entry point", exception);

            sendStartAuthentication(request, response, chain, (AuthenticationException) exception);
        }
        //如果是AccessDeniedException类型异常
        else if (exception instanceof AccessDeniedException){
            Authentication authentication = securityContextHolder.getContext().getAuthentication();
            //判断authentication的来源: 匿名|rememberMe还是通过authenticate
            if (authenticationTrustResolver.isAnonymous(authentication) || authenticationTrustResolver.isRememberMe(authentication)){
                logger.debug("访问被拒绝(Subject is "
                                + (authenticationTrustResolver.isAnonymous(authentication) ? "anonymous" : "没有进行充分的认证")
                                + "); redirecting to authentication entry point", exception);

                sendStartAuthentication(request, response, chain, new InsufficientAuthenticationException(401,
                                "需要完全认证才能访问该资源(未认证账户或认证不充分, 请重新认证)"));
            }else {
                logger.debug("Access is denied (user is not anonymous); delegating to AccessDeniedHandler", exception);
                /**
                 * 如果是 <code>AccessDeniedException</code> 异常,而且当前不是匿名用户，也不是RememberMe, 而是真正经过认证的用户,
                 * 则说明是该用户权限不足, 交由 accessDeniedHandler 处理，缺省告知其权限不足（JSON交互）
                 */
                accessDeniedHandler.handle(request, response, (AccessDeniedException) exception);
            }
        }
    }

    /**
     * 处理AuthenticationException类型异常
     * @param request
     * @param response
     * @param chain
     * @param reason
     * @throws ServletException
     * @throws IOException
     */
    private void sendStartAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, AuthenticationException reason)  throws ServletException, IOException {
        // SEC-112: Clear the SecurityContextHolder's Authentication, as the existing Authentication is no longer considered valid
        // SEC-112：清除SecurityContextHolder的身份验证，因为不再将现有身份验证视为有效
        securityContextHolder.getContext().setAuthentication(null);
        // 这里对requestCache使用HttpSession进行缓存, 应用场景在于使用浏览器并且要求出现访问认证失败并重新认证后, 可以跳转到之前访问的地址
        // TODO: 2019-11-27 目前应用于前后端分离场景, 应考虑Request缓存问题（Session可能不同）, 并完成上述地址跳转场景
        // 这里暂时不做实现
//        requestCache.saveRequest(request, response);
        logger.debug("Calling Authentication entry point.");
        authenticationEntryPoint.commence(request, response, reason);
    }

    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    protected AuthenticationTrustResolver getAuthenticationTrustResolver() {
        return authenticationTrustResolver;
    }

    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        Assert.notNull(accessDeniedHandler, "AccessDeniedHandler required");
        this.accessDeniedHandler = accessDeniedHandler;
    }

    public void setThrowableAnalyzer(ThrowableAnalyzer throwableAnalyzer) {
        this.throwableAnalyzer = throwableAnalyzer;
    }

    public void setAuthenticationTrustResolver(AuthenticationTrustResolver authenticationTrustResolver) {
        this.authenticationTrustResolver = authenticationTrustResolver;
    }

    /**
     * Default implementation of <code>ThrowableAnalyzer</code> which is capable of also
     * unwrapping <code>ServletException</code>s.
     *
     *
     * 翻译:
     * <code> ThrowableAnalyzer </ code>的默认实现，该实现还可以解析<code> ServletException </ code>。
     */
    private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {
        @Override
        protected void initExtractorMap() {
            super.initExtractorMap();
            //往分析器里面添加ServletException与其cause提取器，提供对ServletException的支持
            registerExtractor(ServletException.class, new ThrowableCauseExtractor() {
                @Override
                public Throwable extractCause(Throwable throwable) {
                    ThrowableAnalyzer.verifyThrowableHierarchy(throwable, ServletException.class);
                    return ((ServletException) throwable).getRootCause();
                }
            });
        }
    }
}
