package com.light.security.core.util.matcher;

import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName AndRequestMatcher
 * @Description 所有都匹配则匹配成功, 否则匹配失败
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public final class AndRequestMatcher extends AbstractRequestMatcher{

    private final List<RequestMatcher> requestMatchers;

    public AndRequestMatcher(List<RequestMatcher> requestMatchers){
        Assert.notEmpty(requestMatchers, "requestMatchers 中需要存在至少一个元素");
        if (requestMatchers.contains(null)){
            throw new IllegalArgumentException("requestMatchers 中不能包含 null 元素");
        }
        this.requestMatchers = requestMatchers;
    }

    public AndRequestMatcher(RequestMatcher... requestMatchers){
        this(Arrays.asList(requestMatchers));
    }


    /**
     * 如果一个失败, 则视为匹配失败
     * @param request
     * @return
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher matcher : requestMatchers){
            if (logger.isDebugEnabled()){
                logger.debug("正在使用 {} 尝试匹配", matcher);
            }
            if (!matcher.matches(request)){
                logger.debug("匹配失败");
                return false;
            }
        }
        logger.debug("所有的匹配器都成功匹配, 返回true");
        return true;
    }
}
