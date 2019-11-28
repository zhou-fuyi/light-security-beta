package com.light.security.core.access;

import com.light.security.core.authentication.token.Authentication;

import java.util.Collection;

/**
 * @InterfaceName AccessDecisionVoter
 * @Description 仿照SpringSecurity完成
 * Indicates a class is responsible for voting on authorization decisions.
 * <p>
 * The coordination of voting (ie polling {@code AccessDecisionVoter}s, tallying their
 * responses, and making the final authorization decision) is performed by an
 * {@link com.light.security.core.access.AccessDecisionManager}.
 *
 *
 * 翻译:
 * 负责对授权决策进行投票。
 * <p>
 * 投票的协调（即，对{@code AccessDecisionVoter}进行轮询，计算其响应并做出最终授权决定）
 * 由{@link com.light.security.core.access.AccessDecisionManager}执行。
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public interface AccessDecisionVoter<V> {

    int ACCESS_GRANTED = 1;//授予访问权限
    int ACCESS_ABSTAIN = 0;//弃权
    int ACCESS_DENIED = -1;//拒绝访问

    /**
     * 判断此{@code AccessDecisionVoter}是否能够对传递的{@code ConfigAttribute}进行投票
     * @param attribute
     * @return
     */
    boolean supports(ConfigAttribute attribute);

    /**
     * 判断{@code AccessDecisionVoter}实现是否能够为指示的安全对象类型提供访问控制权。
     * @param clazz
     * @return
     */
    boolean supports(Class<?> clazz);

    /**
     * 评估是否授予访问权限
     * Indicates whether or not access is granted.
     * <p>
     * The decision must be affirmative ({@code ACCESS_GRANTED}), negative (
     * {@code ACCESS_DENIED}) or the {@code AccessDecisionVoter} can abstain (
     * {@code ACCESS_ABSTAIN}) from voting. Under no circumstances should implementing
     * classes return any other value. If a weighting of results is desired, this should
     * be handled in a custom
     * {@link org.springframework.security.access.AccessDecisionManager} instead.
     * <p>
     * Unless an {@code AccessDecisionVoter} is specifically intended to vote on an access
     * control decision due to a passed method invocation or configuration attribute
     * parameter, it must return {@code ACCESS_ABSTAIN}. This prevents the coordinating
     * {@code AccessDecisionManager} from counting votes from those
     * {@code AccessDecisionVoter}s without a legitimate interest in the access control
     * decision.
     * <p>
     *
     *
     * 翻译:
     * <p>
     * 该决定必须是肯定的（{@code ACCESS_GRANTED}），否定的（{@code ACCESS_DENIED}）或{@code AccessDecisionVoter}
     * 可以放弃（= {​​@ code ACCESS_ABSTAIN}）投票。在任何情况下，实现类都不应返回任何其他值。如果需要对结果进行加权，则应
     * 而是在自定义{@link com.light.security.core.access.AccessDecisionManager}中处理。
     * <p>
     * 除非由于传递的方法调用或配置属性参数而专门设计{@code AccessDecisionVoter}来对访问控制决策进行投票，
     * 否则它必须返回{@code ACCESS_ABSTAIN}。这样可以防止协调{@code AccessDecisionManager}计算来自那些
     * {@code AccessDecisionVoter}的选票而对访问控制决策没有合法利益。
     * <p>
     * @param authentication
     * @param object
     * @param configAttributes
     * @return
     */
    int vote(Authentication authentication, V object, Collection<ConfigAttribute> configAttributes);

}
