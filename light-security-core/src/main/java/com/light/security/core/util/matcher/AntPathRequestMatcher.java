package com.light.security.core.util.matcher;

import com.light.security.core.util.ServletUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * @ClassName AntPathRequestMatcher
 * @Description 仿照SpringSecurity完成
 * 主要是对AntPathMatcher的包装
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public final class AntPathRequestMatcher extends AbstractRequestMatcher implements RequestVariablesExtractor{

    private static final String MATCH_ALL = "/**";//匹配所有

    private Matcher matcher;//匹配器
    private final String pattern;
    private final HttpMethod httpMethod;
    private final boolean caseSensitive;

    public AntPathRequestMatcher(String pattern){
        this(pattern, null);
    }

    public AntPathRequestMatcher(String pattern, String httpMethod){
        this(pattern, httpMethod, true);
    }

    public AntPathRequestMatcher(String pattern, String httpMethod, boolean caseSensitive){
        Assert.hasText(pattern, "Pattern cannot be null or empty");
        this.caseSensitive = caseSensitive;
        if (pattern.equals(MATCH_ALL) || pattern.equals("**")) {
            pattern = MATCH_ALL;
            this.matcher = null;
        }else {
            // If the pattern ends with {@code /**} and has no other wildcards or path
            // variables, then optimize to a sub-path match
            if (pattern.endsWith(MATCH_ALL)
                    && (pattern.indexOf('?') == -1 && pattern.indexOf('{') == -1
                    && pattern.indexOf('}') == -1)
                    && pattern.indexOf("*") == pattern.length() - 2) {
                this.matcher = new SubPathMatcher(
                        pattern.substring(0, pattern.length() - 3), caseSensitive);
            }
            else {
                this.matcher = new SpringAntMatcher(pattern, caseSensitive);
            }
        }

        this.pattern = pattern;
        this.httpMethod = StringUtils.hasText(httpMethod) ? HttpMethod.valueOf(httpMethod) : null;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (this.httpMethod != null && StringUtils.hasText(request.getMethod()) && this.httpMethod != valueOf(request.getMethod())){
            if (logger.isDebugEnabled()) {
                logger.debug("Request '" + request.getMethod() + " "
                        + ServletUtils.getServletPath(request) + "'" + " doesn't match '"
                        + this.httpMethod + " " + this.pattern);
            }

            return false;
        }

        if (this.pattern.equals(MATCH_ALL)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Request '" + ServletUtils.getServletPath(request) + "' matched by universal pattern '/**'");
            }

            return true;
        }

        String url = ServletUtils.getServletPath(request);

        if (logger.isDebugEnabled()) {
            logger.debug("Checking match of request : '" + url + "'; against '" + this.pattern + "'");
        }

        return this.matcher.matches(url);
    }

    private HttpMethod valueOf(String method){
        try {
            return HttpMethod.valueOf(method);
        }catch (IllegalArgumentException e){
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Map<String, String> extractUriTemplateVariables(HttpServletRequest request) {
        if (this.matcher == null || !matches(request)) {
            return Collections.emptyMap();
        }
        String url = ServletUtils.getServletPath(request);
        return this.matcher.extractUriTemplateVariables(url);
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AntPathRequestMatcher)) {
            return false;
        }

        AntPathRequestMatcher other = (AntPathRequestMatcher) obj;
        return this.pattern.equals(other.pattern) && this.httpMethod == other.httpMethod
                && this.caseSensitive == other.caseSensitive;
    }

    @Override
    public int hashCode() {
        int code = 31 ^ this.pattern.hashCode();
        if (this.httpMethod != null) {
            code ^= this.httpMethod.hashCode();
        }
        return code;
    }

    private static interface Matcher{

        /**
         * 匹配规则
         * @param path
         * @return
         */
        boolean matches(String path);

        /**
         * 提取URL模板变量
         * For example: For pattern "/hotels/{hotel}" and path "/hotels/1", this method will  return a map containing "hotel"->"1".
         * @param path
         * @return
         */
        Map<String, String> extractUriTemplateVariables(String path);
    }

    private static class SpringAntMatcher implements Matcher{

        private final AntPathMatcher antPathMatcher;

        private final String pattern;

        private SpringAntMatcher(String pattern, boolean caseSensitive){
            this.pattern = pattern;
            this.antPathMatcher = createMatcher(caseSensitive);
        }

        @Override
        public boolean matches(String path) {
            return this.antPathMatcher.match(this.pattern, path);
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return this.antPathMatcher.extractUriTemplateVariables(this.pattern, path);
        }

        private static AntPathMatcher createMatcher(boolean caseSensitive){
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            antPathMatcher.setTrimTokens(false);//指定是否修剪标记化的路径和模式, 默认为false
            antPathMatcher.setCaseSensitive(caseSensitive);
            return antPathMatcher;
        }
    }

    /**
     * Optimized matcher for trailing wildcards
     *
     * 翻译:
     * 针对尾随通配符的优化匹配器
     */
    private static class SubPathMatcher implements Matcher {
        private final String subPath;
        private final int length;
        private final boolean caseSensitive;

        private SubPathMatcher(String subPath, boolean caseSensitive) {
            assert!subPath.contains("*");
            this.subPath = caseSensitive ? subPath : subPath.toLowerCase();
            this.length = subPath.length();
            this.caseSensitive = caseSensitive;
        }

        @Override
        public boolean matches(String path) {
            if (!this.caseSensitive) {
                path = path.toLowerCase();
            }
            return path.startsWith(this.subPath) && (path.length() == this.length || path.charAt(this.length) == '/');
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return Collections.emptyMap();
        }
    }
}
