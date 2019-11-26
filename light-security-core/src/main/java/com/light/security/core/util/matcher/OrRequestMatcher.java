package com.light.security.core.util.matcher;

import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName OrRequestMatcher
 * @Description 有一个匹配则匹配成功, 否则匹配失败
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public class OrRequestMatcher extends AbstractRequestMatcher{

    private final List<RequestMatcher> requestMatchers;

    public OrRequestMatcher(List<RequestMatcher> requestMatchers){
        Assert.notEmpty(requestMatchers, "requestMatchers 需要存在至少一个元素");
        if (requestMatchers.contains(null)){
            throw new IllegalArgumentException("requestMatchers 中不能包含 null 元素");
        }
        this.requestMatchers = requestMatchers;
    }

    public OrRequestMatcher(RequestMatcher... requestMatchers){
        this(Arrays.asList(requestMatchers));
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher matcher : this.requestMatchers){
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to match using " + matcher);
            }
            if (matcher.matches(request)) {
                logger.debug("matched");
                return true;
            }
        }
        logger.debug("No matches found");
        return false;
    }
}
