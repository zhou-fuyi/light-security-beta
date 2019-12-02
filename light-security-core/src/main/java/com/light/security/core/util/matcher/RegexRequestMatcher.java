package com.light.security.core.util.matcher;

import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * @ClassName RegexRequestMatcher
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public class RegexRequestMatcher extends AbstractRequestMatcher {

    private final Pattern pattern;
    private final HttpMethod httpMethod;

    public RegexRequestMatcher(String pattern, String httpMethod){
        this(pattern, httpMethod, false);
    }

    public RegexRequestMatcher(String pattern, String httpMethod, boolean caseInsensitive) {
        if (caseInsensitive) {
            this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }
        else {
            this.pattern = Pattern.compile(pattern);
        }
        this.httpMethod = StringUtils.hasText(httpMethod) ? HttpMethod.valueOf(httpMethod) : null;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (httpMethod != null && request.getMethod() != null
                && httpMethod != valueOf(request.getMethod())) {
            return false;
        }

        String url = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String query = request.getQueryString();

        if (pathInfo != null || query != null) {
            StringBuilder sb = new StringBuilder(url);

            if (pathInfo != null) {
                sb.append(pathInfo);
            }

            if (query != null) {
                sb.append('?').append(query);
            }
            url = sb.toString();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Checking match of request : '" + url + "'; against '" + pattern
                    + "'");
        }

        return pattern.matcher(url).matches();
    }

    private static HttpMethod valueOf(String method) {
        try {
            return HttpMethod.valueOf(method);
        }
        catch (IllegalArgumentException e) {
        }

        return null;
    }
}
