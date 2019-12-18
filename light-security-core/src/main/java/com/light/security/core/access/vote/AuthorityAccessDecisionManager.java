package com.light.security.core.access.vote;

import com.light.security.core.access.AccessDecisionVoter;
import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.AccessDeniedException;
import com.light.security.core.exception.InsufficientAuthenticationException;
import com.light.security.core.exception.NoAccessException;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName AuthorityAccessDecisionManager
 * @Description 权限访问决策管理器
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class AuthorityAccessDecisionManager extends AbstractAccessDecisionManager {

    public AuthorityAccessDecisionManager(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        int deny = 0;
        for (AccessDecisionVoter voter : getDecisionVoters()){
            int result = voter.vote(authentication, object, configAttributes);
            if (logger.isDebugEnabled()) {
                logger.debug("Voter: " + voter + ", returned: " + result);
            }
            switch (result) {
                case AccessDecisionVoter.ACCESS_GRANTED:
                    return;

                case AccessDecisionVoter.ACCESS_DENIED:
                    deny++;

                    break;

                default:
                    break;
            }
        }
        if (deny > 0){
            throw new NoAccessException(403, "没有权限, 拒绝访问");
        }

        // To get this far, every AccessDecisionVoter abstained
        // 如果能执行到这里, 那么表示每一个voter都弃权了, 默认实现中, 会抛出无权访问异常
        //该方法不可继承, 如果想要修改逻辑, 可以通过修改allowIfAllAbstainDecisions为true(默认为false)实现, 或者是自己定义业务
        checkAllowIfAllAbstainDecisions();
    }
}
