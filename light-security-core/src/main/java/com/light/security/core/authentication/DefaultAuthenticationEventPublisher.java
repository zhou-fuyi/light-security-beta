package com.light.security.core.authentication;

import com.light.security.core.authentication.event.*;
import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

/**
 * @ClassName DefaultAuthenticationEventPublisher
 * @Description TODO
 * The default strategy for publishing authentication events.
 * <p>
 * Maps well-known <tt>AuthenticationException</tt> types to events and publishes them via
 * the application context. If configured as a bean, it will pick up the
 * <tt>ApplicationEventPublisher</tt> automatically. Otherwise, the constructor which
 * takes the publisher as an argument should be used.
 * <p>
 * The exception-mapping system can be fine-tuned by setting the
 * <tt>additionalExceptionMappings</tt> as a <code>java.util.Properties</code> object. In
 * the properties object, each of the keys represent the fully qualified classname of the
 * exception, and each of the values represent the name of an event class which subclasses
 * {@link AbstractAuthenticationFailureEvent}
 * and provides its constructor. The <tt>additionalExceptionMappings</tt> will be merged
 * with the default ones.
 *
 *
 * 翻译:
 * 发布身份验证事件的默认策略。
 * <p>
 * 将众所周知的<tt> AuthenticationException </ tt>类型映射到事件，并通过应用程序上下文发布它们。
 * 如果配置为Bean，它将自动拾取<tt> ApplicationEventPublisher </ tt>。 否则，应使用以发布者为参数的构造函数。
 * <p>
 * 可以通过将<tt> additionalExceptionMappings </ tt>设置为<code> java.util.Properties </ code>对象来微调异常映射系统。
 * 在属性对象中，每个键代表异常的完全限定的类名，每个值代表事件类的名称，
 * 该事件类将{@link AbstractAuthenticationFailureEvent}
 * 子类化并提供其 构造函数。 <tt> additionalExceptionMappings </ tt>将与默认的合并。
 *
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class DefaultAuthenticationEventPublisher implements AuthenticationEventPublisher, ApplicationEventPublisherAware {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;
    private final HashMap<String, Constructor<? extends AbstractAuthenticationEvent>> exceptionEventMappings = new HashMap<>();

    public DefaultAuthenticationEventPublisher(){
        this(null);
    }

    public DefaultAuthenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher){
        this.applicationEventPublisher = applicationEventPublisher;
        addMapping(BadCredentialsException.class.getName(),
                AuthenticationFailureBadCredentialsEvent.class);
        addMapping(SubjectNameNotFoundException.class.getName(),
                AuthenticationFailureBadCredentialsEvent.class);
        addMapping(AccountExpiredException.class.getName(),
                AuthenticationFailureExpiredEvent.class);
        addMapping(ProviderNotFoundException.class.getName(),
                AuthenticationFailureProviderNotFoundEvent.class);
        addMapping(DisabledException.class.getName(),
                AuthenticationFailureDisabledEvent.class);
        addMapping(LockedException.class.getName(),
                AuthenticationFailureLockedEvent.class);
        addMapping(AuthenticationServiceException.class.getName(),
                AuthenticationFailureServiceExceptionEvent.class);
        addMapping(CredentialsExpiredException.class.getName(),
                AuthenticationFailureCredentialsExpiredEvent.class);
        addMapping(
                "org.springframework.security.authentication.cas.ProxyUntrustedException",
                AuthenticationFailureProxyUntrustedEvent.class);
    }
    public void publishAuthenticationSuccess(Authentication authentication) {
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));
        }
    }

    public void publishAuthenticationFailure(AuthenticationException exception,
                                             Authentication authentication) {
        Constructor<? extends AbstractAuthenticationEvent> constructor = exceptionEventMappings.get(exception.getClass().getName());
        AbstractAuthenticationEvent event = null;

        if (constructor != null) {
            try {
                event = constructor.newInstance(authentication, exception);
            }
            catch (IllegalAccessException ignored) {
            }
            catch (InstantiationException ignored) {
            }
            catch (InvocationTargetException ignored) {
            }
        }

        if (event != null) {
            if (applicationEventPublisher != null) {
                applicationEventPublisher.publishEvent(event);
            }
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("No event was found for the exception "
                        + exception.getClass().getName());
            }
        }
    }

    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Sets additional exception to event mappings. These are automatically merged with
     * the default exception to event mappings that <code>ProviderManager</code> defines.
     *
     * @param additionalExceptionMappings where keys are the fully-qualified string name
     * of the exception class and the values are the fully-qualified string name of the
     * event class to fire.
     */
    @SuppressWarnings({ "unchecked" })
    public void setAdditionalExceptionMappings(Properties additionalExceptionMappings) {
        Assert.notNull(additionalExceptionMappings,
                "The exceptionMappings object must not be null");
        for (Object exceptionClass : additionalExceptionMappings.keySet()) {
            String eventClass = (String) additionalExceptionMappings.get(exceptionClass);
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass(eventClass);
                Assert.isAssignable(AbstractAuthenticationFailureEvent.class, clazz);
                addMapping((String) exceptionClass,
                        (Class<? extends AbstractAuthenticationFailureEvent>) clazz);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to load authentication event class "
                        + eventClass);
            }
        }
    }

    private void addMapping(String exceptionClass,
                            Class<? extends AbstractAuthenticationFailureEvent> eventClass) {
        try {
            Constructor<? extends AbstractAuthenticationEvent> constructor = eventClass
                    .getConstructor(Authentication.class, AuthenticationException.class);
            exceptionEventMappings.put(exceptionClass, constructor);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Authentication event class "
                    + eventClass.getName() + " has no suitable constructor");
        }
    }
}
