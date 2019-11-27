package com.light.security.core.access;

/**
 * @InterfaceName ConfigAttribute
 * @Description 仿照SpringSecurity完成
 * Stores a security system related configuration attribute.
 *
 * <p>
 * When an
 * {@link org.springframework.security.access.intercept.AbstractSecurityInterceptor} is
 * set up, a list of configuration attributes is defined for secure object patterns. These
 * configuration attributes have special meaning to a {@link RunAsManager},
 * {@link AccessDecisionManager} or <code>AccessDecisionManager</code> delegate.
 *
 * <p>
 * Stored at runtime with other <code>ConfigAttribute</code>s for the same secure object
 * target.
 *
 *
 * 翻译:
 * 存储与安全系统相关的配置属性。
 * <p>
 * 当设置了{@link org.springframework.security.access.intercept.AbstractSecurityInterceptor}，
 * 并为安全对象模式定义了配置属性列表。 这些配置属性对{@link RunAsManager}，
 * {@link AccessDecisionManager} 或<code> AccessDecisionManager </ code>委托具有特殊含义。
 * <p>
 * 在运行时与其他<code> ConfigAttribute </ code>一起存储，用于同一安全对象目标。
 *
 * @Author ZhouJian
 * @Date 2019-11-27
 */
public interface ConfigAttribute {

    /**
     * If the <code>ConfigAttribute</code> can be represented as a <code>String</code> and
     * that <code>String</code> is sufficient in precision to be relied upon as a
     * configuration parameter by a {@link RunAsManager}, {@link AccessDecisionManager} or
     * <code>AccessDecisionManager</code> delegate, this method should return such a
     * <code>String</code>.
     * <p>
     * If the <code>ConfigAttribute</code> cannot be expressed with sufficient precision
     * as a <code>String</code>, <code>null</code> should be returned. Returning
     * <code>null</code> will require any relying classes to specifically support the
     * <code>ConfigAttribute</code> implementation, so returning <code>null</code> should
     * be avoided unless actually required.
     *
     *
     * 翻译:
     * 如果<code> ConfigAttribute </ code>可以表示为<code> String </ code>，并且该<code> String </ code>
     * 的精度足以由{@link用作配置参数 RunAsManager}，{@link AccessDecisionManager}
     * 或<code> AccessDecisionManager </ code>委托，此方法应返回这样的<code> String </ code>。
     * <p>
     * 如果不能以足够的精度将<code> ConfigAttribute </ code>表示为<code> String </ code>，则应返回<code> null </ code>。
     * 返回<code> null </ code>将需要特定的依赖类来专门支持<code> ConfigAttribute </ code>实现，因此，除非实际需要，
     * 否则应避免返回<code> null </ code>。
     *
     * 小声BB:
     * 简而言之, 这里便是实现权限对比的关键, 我在这里的实现会返回一个可以代表一则权限数据的字符串, 我更愿意称之为权限点或权限编码,
     * 在数据表中具有唯一性, 它不同于URL匹配表达式(/api/v1/user/list)或是权限表达式(user:delete), 它是除此之外的另一个属性,
     * 在目前的实现中, 是通过URL匹配表达式和权限编码结合进行, 暂时未考虑权限表达式的方式(注解实现吗, 这样便会耦合到业务层面了,
     * 这个不重要, 再说吧再说吧)
     * @return
     */
    String getAttribute();

}
