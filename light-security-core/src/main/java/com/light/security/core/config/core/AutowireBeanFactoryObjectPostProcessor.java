package com.light.security.core.config.core;

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
 *
 * 注意: 核心便是 --> 使得IOC容器外的Bean可以使用依賴注入——AutowireCapableBeanFactory
 *
 * 简单理解 --> 便是使得没有被IOC容器管理的但是已经实例化的Bean可以使用IOC容器的依赖注入(如@Autowired)能力
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
            /**
             * 这里会调用{@link InitializingBean#afterPropertiesSet()}方法, 所以如果你使用这样的方式进行依赖注入, 千万不要在
             * {@link InitializingBean#afterPropertiesSet()}方法中验证待注入对象的可用性(比如不能为null验证), 因为这个方法会在
             * 尝试依赖注入前被调用
             */
            result = (T) this.autowireBeanFactory.initializeBean(object, object.toString());
        }
        catch (RuntimeException e) {
            Class<?> type = object.getClass();
            throw new RuntimeException("Could not postProcess " + object + " of type " + type, e);
        }
        /**
         * 这里便是处理 object 的依赖注入问题
         */
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
