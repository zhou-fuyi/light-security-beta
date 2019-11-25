package com.light.security.core.authentication.context;

import com.light.security.core.authentication.token.Authentication;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * @ClassName DefaultSecurityContext
 * @Description <code>SecurityContext</code>的默认实现
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class DefaultSecurityContext implements SecurityContext {

    private Authentication authentication;

    public DefaultSecurityContext(){}

    public DefaultSecurityContext(Authentication authentication){
        Assert.notNull(authentication, "构造器不接受空值参数 --> authentication is null");
        this.authentication = authentication;
    }

    @Override
    public Authentication getAuthentication() {
        return this.authentication;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public int hashCode() {
        if (this.authentication == null){
            return -1;
        }else {
            return this.authentication.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DefaultSecurityContext){
            DefaultSecurityContext temp = (DefaultSecurityContext) obj;
            if ((this.getAuthentication()) == null && (temp.getAuthentication() == null)){
                return true;
            }

            if ((this.getAuthentication() != null) && (temp.getAuthentication() != null) && this.getAuthentication().equals(temp.getAuthentication())){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
