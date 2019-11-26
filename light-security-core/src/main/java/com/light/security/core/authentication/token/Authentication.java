package com.light.security.core.authentication.token;

import com.light.security.core.access.role.GrantedRole;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;

/**
 * @InterfaceName Authentication
 * @Description 认证顶层接口
 * @Author ZhouJian
 * @Date 2019-11-19
 */
public interface Authentication extends Principal, Serializable {

    /**
     * 用于返回认证成功后当前账户持有的所有角色集合
     * @return
     */
    Collection<? extends GrantedRole> getGrantedRoles();

    /**
     * 返回当前账户是否已认证
     * @return 返回false表示未认证
     */
    boolean isAuthenticated();

    /**
     * 设置账户的认证状态, 若参数异常会抛出异常(如在底层中设置认证状态为true则会抛出异常)
     * 详细设计可以参见代码实现, 注意本接口的抽象类和具体实现中的方法实现情况.
     * @param isAuthenticated
     * @throws IllegalArgumentException
     */
    void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;

    /**
     * 获取账户的一些额外的细节, 具体内容可自定义
     * @return
     */
    Object getDetails();

    /**
     * 获取账户的主体情况, 通常为账户基本信息, 如用户名等, 注意账户凭据的脱敏处理
     * @return
     */
    Object getSubject();

    /**
     * 获取账户的凭据, 通常情况下, 不会直接存储和返回账户凭据, 进而避免凭据泄露
     * @return
     */
    Object getCredentials();

}
