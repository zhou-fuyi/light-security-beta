package com.light.security.core.authentication.context;

/**
 * @ClassName SecurityContext
 * @Description 仿照SpringSecurity中的<code>SecurityContext</code>的存储策略， 用于当前线程内节点通信使用
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class InternalSecurityContext extends AbstractThreadLocalContext<SecurityContext> {

    @Override
    public SecurityContext createEmptyContent() {
        return new DefaultSecurityContext();
    }
}
