package com.light.security.core.config.core;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName SecurityConfigureAdapter
 * @Description 仿造SpringSecurity完成
 * A base class for {@link SecurityConfigurer} that allows subclasses to only implement
 * the methods they are interested in. It also provides a mechanism for using the
 * {@link SecurityConfigurer} and when done gaining access to the {@link SecurityBuilder}
 * that is being configured.
 *
 *
 * 翻译:
 * {@link SecurityConfigurer}的基类，它允许子类仅实现它们感兴趣的方法。
 * 它还提供了使用{@link SecurityConfigurer}以及完成对正在配置的{@link SecurityBuilder}的访问权限的机制。
 *
 * 小声BB:
 * 该抽象类主要面向于类似于<code> Filter </code>这样的系统组件的配置, 不同于{@link WebSecurityConfigurer}
 * 的是, 后置只针对{@link HttpSecurity}提供配置
 * @Author ZhouJian
 * @Date 2019-11-29
 */
public abstract class SecurityConfigurerAdapter<T, B extends SecurityBuilder<T>> implements SecurityConfigurer<T, B> {

    /**
     * 用于存储当前配置器辅助的构建器对象
     * 其赋值过程可以参见{@link AbstractConfiguredSecurityBuilder#apply(SecurityConfigurerAdapter)},
     * 就是在这个方法执行过程中设置了{@link #builder}
     */
    private B builder;

    // 后置处理器
    private CompositeObjectPostProcessor objectPostProcessor = new CompositeObjectPostProcessor();

    @Override
    public void init(B builder) throws Exception {

    }

    @Override
    public void configure(B builder) throws Exception {

    }

    /**
     * 使用{@link SecurityConfigurer}完成后，返回{@link SecurityBuilder}。
     * 这对于方法链接很有用。
     * @return {@link SecurityBuilder}
     */
    public B and(){
        return getBuilder();
    }

    /**
     * 后处理器
     * @param object
     * @param <T>
     * @return
     */
    protected <T> T postProcess(T object){
        return (T) this.objectPostProcessor.postProcess(object);
    }

    /**
     * 添加后置处理器
     * @param objectPostProcessor
     */
    public void addObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor){
        this.objectPostProcessor.addObjectPostProcessor(objectPostProcessor);
    }

    public void setBuilder(B builder){
        this.builder = builder;
    }

    /**
     * 获取当前类中保存的{@link SecurityBuilder}, 不能为null
     * @return
     */
    protected B getBuilder() {
        if (builder == null){
            throw new IllegalArgumentException("builder 对象不能为 null");
        }
        return this.builder;
    }

    /**
     * An {@link ObjectPostProcessor} that delegates work to numerous
     * {@link ObjectPostProcessor} implementations.
     *
     * 一个{@link ObjectPostProcessor}，它将工作委托给许多{@link ObjectPostProcessor}实现。
     *
     * @author Rob Winch
     */
    private static final class CompositeObjectPostProcessor implements ObjectPostProcessor<Object> {

        private List<ObjectPostProcessor<? extends Object>> postProcessors = new ArrayList<ObjectPostProcessor<?>>();

        /**
         * 存储一组{@link ObjectPostProcessor}, 在使用该后置处理器时, 会根据传入对象到维护的
         * {@link #postProcessors}进行查找(查找使用 <code>isAssignableFrom</code> 对比),
         * 如果存在则使用该对应的后置处理器进行处理, 否则便不进行处理, 返回传入数据
         * @param object
         * @return
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Object postProcess(Object object) {
            for (ObjectPostProcessor opp : postProcessors) {
                Class<?> oppClass = opp.getClass();
                Class<?> oppType = GenericTypeResolver.resolveTypeArgument(oppClass, ObjectPostProcessor.class);
                if (oppType == null || oppType.isAssignableFrom(object.getClass())) {
                    object = opp.postProcess(object);
                }
            }
            return object;
        }

        /**
         * Adds an {@link ObjectPostProcessor} to use
         * 新增一则后置处理器
         * @param objectPostProcessor the {@link ObjectPostProcessor} to add
         * @return true if the {@link ObjectPostProcessor} was added, else false
         */
        private boolean addObjectPostProcessor(ObjectPostProcessor<? extends Object> objectPostProcessor) {
            boolean result = this.postProcessors.add(objectPostProcessor);
            Collections.sort(postProcessors, AnnotationAwareOrderComparator.INSTANCE);
            return result;
        }
    }
}
