package com.light.security.core.authentication.token;

import com.light.security.core.access.role.GrantedRole;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;

/**
 * @ClassName AnonymousAuthenticationToken
 * @Description 仿照SpringSecurity完成
 * Represents an anonymous <code>Authentication</code>.
 *
 * 翻译:
 * 代表匿名<code> Authentication </ code>。
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public class AnonymousAuthenticationToken extends AbstractAuthenticationToken implements Serializable {

    private final Object subject;
    private final int keyHash;

    public AnonymousAuthenticationToken(String key, Object subject, Collection<? extends GrantedRole> roles) {
        this(extractKeyHash(key), subject, roles);
    }

    private AnonymousAuthenticationToken(Integer keyHash, Object subject, Collection<? extends GrantedRole> roles){
        super(roles);
        if (subject == null || "".equals(subject)){
            throw new IllegalArgumentException("构造器不接受空值参数 --> subject is null or '' ");
        }
        this.keyHash = keyHash;
        this.subject = subject;
        setAuthenticated(true);
    }

    @Override
    public Object getSubject() {
        return subject;
    }

    /**
     * Always returns an empty <code>String</code>
     * @return
     */
    @Override
    public Object getCredentials() {
        return "";
    }

    private static Integer extractKeyHash(String key){
        Assert.hasLength(key, "key 不能为null 或 '' ");
        return key.hashCode();
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj instanceof AnonymousAuthenticationToken) {
            AnonymousAuthenticationToken test = (AnonymousAuthenticationToken) obj;

            if (this.getKeyHash() != test.getKeyHash()) {
                return false;
            }

            return true;
        }

        return false;
    }

    public int getKeyHash() {
        return keyHash;
    }
}
