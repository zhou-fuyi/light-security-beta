package com.light.security.core.authentication;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.token.AnonymousAuthenticationToken;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AuthenticationException;
import com.light.security.core.exception.BadCredentialsException;
import org.springframework.util.Assert;

/**
 * @ClassName AnonymousAuthenticationProvider
 * @Description 匿名对象的认证实现
 * @Author ZhouJian
 * @Date 2019-12-18
 */
public class AnonymousAuthenticationProvider implements AuthenticationProvider {

    private String key;

    public AnonymousAuthenticationProvider(String key){
        Assert.hasLength(key, "构造器不接受空值参数 --> key is null or empty");
        this.key = key;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())){
            return null;
        }
        if (this.key.hashCode() != ((AnonymousAuthenticationToken) authentication).getKeyHash()){
            throw new BadCredentialsException(401, "秘钥对比失败");
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AnonymousAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public String getKey() {
        return key;
    }
}
