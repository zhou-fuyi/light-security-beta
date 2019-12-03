package com.light.security.core.authentication.entrypoint;

import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * @ClassName DelegatingAuthenticationEntryPoint
 * @Description DelegatingAuthenticationEntryPoint
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class DelegatingAuthenticationEntryPoint  implements AuthenticationEntryPoint, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints;
    private AuthenticationEntryPoint defaultEntryPoint;

    public DelegatingAuthenticationEntryPoint(
            LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints) {
        this.entryPoints = entryPoints;
    }

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        for (RequestMatcher requestMatcher : entryPoints.keySet()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to match using " + requestMatcher);
            }
            if (requestMatcher.matches(request)) {
                AuthenticationEntryPoint entryPoint = entryPoints.get(requestMatcher);
                if (logger.isDebugEnabled()) {
                    logger.debug("Match found! Executing " + entryPoint);
                }
                entryPoint.commence(request, response, authException);
                return;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("No match found. Using default entry point " + defaultEntryPoint);
        }

        // No EntryPoint matched, use defaultEntryPoint
        defaultEntryPoint.commence(request, response, authException);
    }

    /**
     * EntryPoint which is used when no RequestMatcher returned true
     */
    public void setDefaultEntryPoint(AuthenticationEntryPoint defaultEntryPoint) {
        this.defaultEntryPoint = defaultEntryPoint;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(entryPoints, "entryPoints must be specified");
        Assert.notNull(defaultEntryPoint, "defaultEntryPoint must be specified");
    }
}
