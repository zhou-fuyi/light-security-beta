package com.light.security.core.util.matcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @InterfaceName RequestVariablesExtractor
 * @Description An interface for extracting URI variables from the {@link HttpServletRequest}.
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public interface RequestVariablesExtractor {

    /**
     * Extract URL template variables from the request.
     *
     * 提取URL模板变量
     *
     * @param request the HttpServletRequest to obtain a URL to extract the variables from
     * @return the URL variables or empty if no variables are found
     */
    Map<String, String> extractUriTemplateVariables(HttpServletRequest request);

}
