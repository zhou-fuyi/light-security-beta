package com.light.security.authentication.subject;

import com.light.security.access.role.GrantedRole;

import java.io.Serializable;
import java.util.Collection;

/**
 * @InterfaceName Subject
 * @Description 账户主体情况, 用于盛放具体的业务情况下真实账户数据
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface SubjectDetail extends Serializable {

    /**
     * 获取账户持有的角色集合
     * @return
     */
    Collection<? extends GrantedRole> getRoles();

    /**
     * 获取账户密码, 通常已经进行了密码脱敏处理
     * @return
     */
    String getPassword();

    /**
     * 获取账户主体名, 常为用户名
     * @return
     */
    String getSubjectName();

    /**
     * 返回账户是否未过期
     * @return <code>true</code> 表示账户未过期
     */
    boolean isAccountNonExpired();

    /**
     * 返回是否当前账户为非锁定账户
     * @return <code>true</code> 表示当前账户未锁定
     */
    boolean isAccountNonLocked();

    /**
     * 返回当前账户的凭据是否未过期
     * @return <code>true</code> 表示当前账户凭据未过期
     */
    boolean isCredentialsNonExpired();

    /**
     * 返回当前账户是否已启用
     * @return <code>true</code> 表示账户已启用
     */
    boolean isEnabled();
}
