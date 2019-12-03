package com.light.security.core.config.core;

import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AutowireBeanFactoryObjectPostProcessor
 * @Description 仿照SpringSecurity完成
 * Allows registering Objects to participate with an {@link AutowireCapableBeanFactory}'s
 * post processing of {@link Aware} methods, {@link InitializingBean#afterPropertiesSet()}
 * , and {@link DisposableBean#destroy()}.
 *
 *
 * 翻译:
 * 允许注册对象参与{@link AutowireCapableBeanFactory}对{@link Aware}方法，
 * {@ link InitializingBean＃afterPropertiesSet（）}和{@link DisposableBean＃destroy（）}的后期处理。
 *
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class AutowireBeanFactoryObjectPostProcessor implements ObjectPostProcessor<Object>, DisposableBean, SmartInitializingSingleton {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AutowireCapableBeanFactory autowireBeanFactory;
    private final List<DisposableBean> disposableBeans = new ArrayList<DisposableBean>();
    private final List<SmartInitializingSingleton> smartSingletons = new ArrayList<SmartInitializingSingleton>();

    public AutowireBeanFactoryObjectPostProcessor(AutowireCapableBeanFactory autowireBeanFactory) {
        Assert.notNull(autowireBeanFactory, "autowireBeanFactory cannot be null");
        this.autowireBeanFactory = autowireBeanFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.security.config.annotation.web.Initializer#initialize(java.
     * lang.Object)
     */
    @SuppressWarnings("unchecked")
    public <T> T postProcess(T object) {
        if (object == null) {
            return null;
        }
        T result = null;
        try {
            result = (T) this.autowireBeanFactory.initializeBean(object, object.toString());
        }
        catch (RuntimeException e) {
            Class<?> type = object.getClass();
            throw new RuntimeException("Could not postProcess " + object + " of type " + type, e);
        }
        this.autowireBeanFactory.autowireBean(object);
        if (result instanceof DisposableBean) {
            this.disposableBeans.add((DisposableBean) result);
        }
        if (result instanceof SmartInitializingSingleton) {
            this.smartSingletons.add((SmartInitializingSingleton) result);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.SmartInitializingSingleton#afterSingletonsInstantiated()
     */
    @Override
    public void afterSingletonsInstantiated() {
        for(SmartInitializingSingleton singleton : smartSingletons) {
            singleton.afterSingletonsInstantiated();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        for (DisposableBean disposable : this.disposableBeans) {
            try {
                disposable.destroy();
            }
            catch (Exception error) {
                this.logger.error("销毁程序异常: {}", error);
            }
        }
    }

}
