package com.light.security.core.filter;

import com.light.security.core.authentication.context.SecurityContext;
import com.light.security.core.authentication.context.holder.HttpRequestResponseHolder;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.authentication.context.repository.SecurityContextRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName SecurityContextPretreatmentFilter
 * @Description <code>SecurityContext</code>预处理过滤器, 从缓存中尝试为SecurityContext加载有效数据, 为后续的操作提供支持
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class SecurityContextPretreatmentFilter extends GenericFilter{

    private static String PROCESS_URL = "/*";

    /**
     * 负责加载和存储SecurityContext
     */
    private SecurityContextRepository securityContextRepository;
    //SecurityContext的数据持有者, 内部解决方案为ThreadLocal
    private SecurityContextHolder securityContextHolder;

    public SecurityContextPretreatmentFilter(){
        setProcessUrl(PROCESS_URL);
    }

    public SecurityContextRepository getSecurityContextRepository() {
        return securityContextRepository;
    }

    public void setSecurityContextRepository(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    public SecurityContextHolder getSecurityContextHolder() {
        return securityContextHolder;
    }

    public void setSecurityContextHolder(SecurityContextHolder securityContextHolder) {
        this.securityContextHolder = securityContextHolder;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
        SecurityContext contextBeforeChainExecution = securityContextRepository.loadContext(requestResponseHolder);
        try {
            securityContextHolder.setContent(contextBeforeChainExecution);
            chain.doFilter(request, response);
        }finally {
            SecurityContext contextAfterChainExecution = securityContextHolder.getContext();

            //Crucial removal of SecurityContextHolder contents - do this before anything
            //至关重要的操作,将ThreadLocal中的数据清除, 听说可以避免一些问题
            /**
             * 节选自：https://www.iteye.com/blog/fengyilin-2411839
             * 其中在请求结束后清除SecurityContextHolder中的SecurityContext的操作是必须的，
             * 因为默认情况下SecurityContextHolder会把SecurityContext存储到ThreadLocal中，
             * 而这个thread刚好是存在于servlet容器的线程池中的，如果不清除，当后续请求又从
             * 线程池中分到这个线程时，程序就会拿到错误的认证信息
             */
            securityContextHolder.cleanContext();

            securityContextRepository.saveContext(contextAfterChainExecution, request, response);
            if (logger.isDebugEnabled()){
                logger.debug("SecurityContextHolder 持有的ThreadLocal容器已经被清除, 当前请求处理完成.");
            }
        }
    }
}
