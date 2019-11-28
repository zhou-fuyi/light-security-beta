package com.light.security.core.access.event;

/**
 * @ClassName PublicInvocationEvent
 * @Description 仿照SpringSecurity完成
 * Event that is generated whenever a public secure object is invoked.
 * <p>
 * A public secure object is a secure object that has no <code>ConfigAttribute</code>s
 * defined. A public secure object will not cause the <code>SecurityContextHolder</code>
 * to be inspected or authenticated, and no authorization will take place.
 * </p>
 * <p>
 * Published just before the secure object attempts to proceed.
 * </p>
 *
 *
 * 翻译:
 * 每当调用公共安全对象时生成的事件。
 * <p>
 * 公共安全对象是未定义<code> ConfigAttribute </ code>的安全对象。 公共安全对象不会导致对
 * <code> SecurityContextHolder </ code>进行检查或身份验证，并且不会进行任何授权。
 * </ p>
 * <p>
 * 在安全对象尝试继续之前发布。
 * </ p>
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class PublicInvocationEvent extends AbstractAuthorizationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public PublicInvocationEvent(Object source) {
        super(source);
    }
}
