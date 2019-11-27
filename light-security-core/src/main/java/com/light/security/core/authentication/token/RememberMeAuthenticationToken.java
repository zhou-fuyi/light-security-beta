package com.light.security.core.authentication.token;

import com.light.security.core.access.role.GrantedRole;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * @ClassName RememberMeAuthenticationToken
 * @Description 仿照 SpringSecurity 完成
 * Represents a remembered <code>Authentication</code>.
 * <p>
 * A remembered <code>Authentication</code> must provide a fully valid
 * <code>Authentication</code>, including the <code>GrantedAuthority</code>s that apply.
 *
 *
 * 翻译:
 * 表示记住的<code> Authentication </ code>。
 * <p>
 * 记住的<code> Authentication </ code>必须提供完全有效的<code> Authentication </ code>，包括适用的<code> GrantedAuthority </ code>。
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public class RememberMeAuthenticationToken extends AbstractAuthenticationToken {

    private final Object subject;
    private final int keyHash;

    public RememberMeAuthenticationToken(String key, Object subject, Collection<? extends GrantedRole> roles) {
        super(roles);
        if (StringUtils.isEmpty(key) || (subject == null) || ("".equals(subject))) {
            throw new IllegalArgumentException("构造器不接受空值参数 --> key is null or '' (or subject is null or '' )");
        }
        this.keyHash = key.hashCode();
        this.subject = subject;
        setAuthenticated(true);
    }

    /**
     * Private Constructor to help in Jackson deserialization.
     *
     *
     * 翻译:
     * 私人构造函数，以帮助Jackson进行反序列化。(未知领域  -->  `` _ ``)
     * @param keyHash
     * @param subject
     * @param roles
     */
    private RememberMeAuthenticationToken(Integer keyHash, Object subject, Collection<? extends GrantedRole> roles) {
        super(roles);

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

    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken test = (RememberMeAuthenticationToken) obj;

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
