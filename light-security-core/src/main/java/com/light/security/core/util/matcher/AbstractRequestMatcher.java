package com.light.security.core.util.matcher;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName AbstractRequestMatcher
 * @Description <code>HttpServletRequest</code>匹配策略通用实现
 * @Author ZhouJian
 * @Date 2019-11-26
 */
public abstract class AbstractRequestMatcher implements RequestMatcher {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // TODO: 2019-11-26 目前的这个通用实现相当的鸡肋, 留待以后尝试改进
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
