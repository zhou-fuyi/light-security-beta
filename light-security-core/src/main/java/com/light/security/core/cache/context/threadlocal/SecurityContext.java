package com.light.security.core.cache.context.threadlocal;

import com.light.security.core.authentication.token.Authentication;

/**
 * @ClassName SecurityContext
 * @Description 仿照SpringSecurity中的<code>SecurityContext</code>， 用于当前线程内节点通信使用
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class SecurityContext extends AbstractThreadLocalContextCache<Authentication> {
}
