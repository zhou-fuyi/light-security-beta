package com.light.security.core.access.vote;

import com.light.security.core.access.AccessDecisionManager;
import com.light.security.core.access.AccessDecisionVoter;
import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.exception.AccessDeniedException;
import com.light.security.core.exception.NoAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @ClassName AbstractAccessDecisionManager
 * @Description 仿照SpringSecurity完成
 * Abstract implementation of {@link AccessDecisionManager}.
 *
 * <p>
 * Handles configuration of a bean context defined list of {@link com.light.security.core.access.AccessDecisionVoter}s
 * and the access control behaviour if all voters abstain from voting (defaults to deny access).
 *
 *
 * 翻译:
 * {@link AccessDecisionManager}的抽象实现。
 *
 * <p>
 * 如果所有投票者都不投票，则处理{@link com.light.security.core.access.AccessDecisionVoter}的bean上下文定义列表的配置和访问控制行为（默认为拒绝访问）
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public abstract class AbstractAccessDecisionManager implements AccessDecisionManager, InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final List<AccessDecisionVoter<? extends Object>> decisionVoters;
    //如果全部弃权决定, 则允许一些操作的标志位
    private boolean allowIfAllAbstainDecisions = false;

    protected AbstractAccessDecisionManager(List<AccessDecisionVoter<? extends Object>> decisionVoters){
        Assert.notEmpty(decisionVoters, "构造器不接受空值参数 --> decisionVoters is null or empty");
        this.decisionVoters = decisionVoters;
    }

    protected final void checkAllowIfAllAbstainDecisions(){
        if (!this.allowIfAllAbstainDecisions){
            throw new NoAccessException(403, "拒绝访问");
        }
    }

    public List<AccessDecisionVoter<? extends Object>> getDecisionVoters() {
        return decisionVoters;
    }

    public void setAllowIfAllAbstainDecisions(boolean allowIfAllAbstainDecisions) {
        this.allowIfAllAbstainDecisions = allowIfAllAbstainDecisions;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(decisionVoters, "decisionVoters不能为null 或 空");
    }

    @Override
    public boolean supports(Class<?> clazz) {
        for (AccessDecisionVoter voter : this.decisionVoters){
            if (!voter.supports(clazz)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        for (AccessDecisionVoter voter : this.decisionVoters){
            if (voter.supports(attribute)){
                return true;
            }
        }
        return false;
    }
}
