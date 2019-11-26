package com.light.security.core.filter;

import com.light.security.core.access.handler.AccessDeniedHandler;
import com.light.security.core.access.handler.AccessDeniedHandlerImpl;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AccessDeniedException;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.util.ThrowableAnalyzer;
import com.light.security.core.util.ThrowableCauseExtractor;

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

    private SecurityContextHolder securityContextHolder;

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
                target = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
            }
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

    private void handleLightSecurityException(HttpServletRequest request, HttpServletResponse response, FilterChain chain, RuntimeException exception) throws IOException, ServletException {
        if (exception instanceof AuthenticationException){
            logger.debug("Authentication exception occurred; redirecting to authentication entry point", exception);

            sendStartAuthentication(request, response, chain, (AuthenticationException) exception);
        }else if (exception instanceof AccessDeniedException){
            Authentication authentication = securityContextHolder.getContext().getAuthentication();
            //判断authentication的来源: 匿名|rememberMe还是通过authenticate
            // TODO: 2019-11-26 未完成 
        }
    }

    private void sendStartAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, AuthenticationException exception)  throws ServletException, IOException {
        // SEC-112: Clear the SecurityContextHolder's Authentication, as the
        // existing Authentication is no longer considered valid
        securityContextHolder.getContext().setAuthentication(null);
//        requestCache.saveRequest(request, response);
        logger.debug("Calling Authentication entry point.");
//        authenticationEntryPoint.commence(request, response, reason);
        // TODO: 2019-11-26 未完成 
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
