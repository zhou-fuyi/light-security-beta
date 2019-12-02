package com.light.security.core.config.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @ClassName AbstractConfiguredSecurityBuilder
 * @Description {@link SecurityBuilder}的第二层级抽象实现, 继承了第一层抽象实现{@link AbstractSecurityBuilder}
 * 在这里维护一组配置器 {@link #configurers}, 用于存储系统配置的配置器, 使用配置器进行目前对象的构建辅助, 将细节分解
 * 到各个配置器上, 好比小说里的身化万千(但是又有点区别, 更像是被切割了, 单独的构建器并不是一个独立的目前对象, 而是这万千
 * 配置器合在一起加上地利人和(打铁自身硬的 {@link B} ) 最后构造了 {@link T} ).
 * 这里将构建策略分解, 可以细化细节, 使得更加灵活的控制每一个节点, 也加入了更多的可能, 细思极恐啊
 *
 * 这算是分治思想的一种实现吗 ?
 * </code>
 * @Author ZhouJian
 * @Date 2019-11-29
 *
 * 小声BB:
 * 就功能实现上来说, 其实可以将具体功能下沉到具体的实现类中, 这里应该是可以不需要B的泛型的, 但是这样设计便更加的通用(这是一句废话)
 * 反正我是抄的SpringSecurity, 没事, 我还能抄
 *
 * @param <T> 表示需要被构建的对象
 * @param <B> 表示用来构建<code> T </code> 的构建器对象类型, 这里限制了B的上界为<code> B extends SecurityBuilder<T> </code>, 取名 Builder
 */
public abstract class AbstractConfiguredSecurityBuilder<T, B extends SecurityBuilder<T>> extends AbstractSecurityBuilder<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 以下两个容器都使用了{@link SecurityConfigurer<T, B>}作为泛型上界, 加大了范围, 更加了灵活, 提高了拓展性
     */
    //用于存放当前需要构建对象需要的配置器
    private final LinkedHashMap<Class<? extends SecurityConfigurer<T, B>>, List<SecurityConfigurer<T, B>>> configurers = new LinkedHashMap();

    // 用于记录在初始化期间添加进来的 SecurityConfigurer
    private final List<SecurityConfigurer<T, B>> configurersAddedInInitializing = new ArrayList<SecurityConfigurer<T, B>>();

    //用于存放一些用于共享实例对象
    private final Map<Class<? extends Object>, Object> sharedObjects = new HashMap<>();

    private BuildState buildState = BuildState.UN_BUILT;//默认为未构建状态

    /**
     * 用于标识是否允许存储相同类型的configurer, 默认情况下当然是不允许的咯, 在构造函数中设定
     */
    private final boolean allowConfigurersOfSameType;

    private ObjectPostProcessor<Object> objectPostProcessor;

    /**
     * 创建实例, 默认 allowConfigurersOfSameType 为 false
     * @param objectPostProcessor
     */
    protected AbstractConfiguredSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor){
        this(objectPostProcessor, false);
    }

    /**
     * 创建实例 可以根据传入参数指定 allowConfigurersOfSameType
     * @param objectPostProcessor
     * @param allowConfigurersOfSameType
     */
    protected AbstractConfiguredSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor, boolean allowConfigurersOfSameType){
        Assert.notNull(objectPostProcessor, "构造器不接受空值参数 --> objectPostProcessor is null");
        this.objectPostProcessor = objectPostProcessor;
        this.allowConfigurersOfSameType = allowConfigurersOfSameType;
    }

    /**
     * 和{@link #build()}与{@link #getTarget()}类似, 都会先检查一下当前的构建状态
     * @return
     */
    public T getOrBuild(){
        if (isUnBuild()){
            try {
                return build();
            } catch (Exception e) {
                logger.error("build 构建对象失败, 将返回 null", e);
                return null;
            }
        }else {
            return getTarget();
        }
    }

    /**
     * Applies a {@link SecurityConfigurerAdapter} to this {@link SecurityBuilder} and
     * invokes {@link SecurityConfigurerAdapter#setBuilder(SecurityBuilder)}.
     *
     *将{@link SecurityConfigurerAdapter}应用于此{@link SecurityBuilder}并调用
     * {@link SecurityConfigurerAdapter＃setBuilder（SecurityBuilder）}
     *
     * 注意这里限制了{@link C}的上界为<code> SecurityConfigureAdapter<T, B> </code>,
     * 界限范围小于另一apply方法, 但是本方法是为了{@link SecurityConfigurerAdapter}量身定做的
     * @param configurer
     * @param <C>
     * @return
     * @throws Exception
     */
    public <C extends SecurityConfigurerAdapter<T, B>> C apply(C configurer) throws Exception{
        configurer.addPostProcessor(objectPostProcessor);
        configurer.setBuilder((B) this);
        add(configurer);
        return configurer;
    }

    /**
     * Applies a {@link SecurityConfigurer} to this {@link SecurityBuilder} overriding any
     * {@link SecurityConfigurer} of the exact same class. Note that object hierarchies
     * are not considered.
     *
     * 将{@link SecurityConfigurer}应用于此{@link SecurityBuilder}，以覆盖完全相同类的任何{@link SecurityConfigurer}。 请注意，不考虑对象层次结构。
     *
     * 注意这里限制了{@link C}的上界为<code> SecurityConfigurer<T, B> </code>
     * @param configurer
     * @param <C>
     * @return
     * @throws Exception
     */
    public <C extends SecurityConfigurer<T, B>> C apply(C configurer) throws Exception {
        add(configurer);
        return configurer;
    }

    /**
     * 添加共享数据
     * @param sharedObject
     * @param object
     * @param <C>
     */
    public <C> void setSharedObject(Class<C> sharedObject, C object){
        this.sharedObjects.put(sharedObject, object);
    }

    /**
     * 根据类型(Class<C> sharedType)获取指定的共享数据
     * @param sharedType
     * @param <C>
     * @return C
     */
    public <C> C getSharedObject(Class<C> sharedType){
        return (C) this.sharedObjects.get(sharedType);
    }

    /**
     * 获取共享数据
     * @return  Map<Class<? extends Object>, Object>
     */
    public Map<Class<? extends Object>, Object> getSharedObjects(){
        return this.sharedObjects;
    }

    /**
     * 添加配置器
     * @param configurer
     * @param <C>
     * @throws Exception
     */
    private <C extends SecurityConfigurer<T, B>> void add(C configurer) throws Exception{
        Assert.notNull(configurer, "configurer is null");
        Class<? extends SecurityConfigurer<T, B>> clazz = (Class<? extends SecurityConfigurer<T, B>>) configurer.getClass();
        synchronized (configurers){
            if (buildState.isConfigured()){
                throw new IllegalArgumentException("不能将配置器: " + configurer + "配置到已经完成构建的对象上");
            }
            List<SecurityConfigurer<T, B>> configurerList = allowConfigurersOfSameType ? this.configurers.get(clazz) : null;
            if (configurerList == null){
                configurerList = new ArrayList<SecurityConfigurer<T, B>>(1);
            }
            configurerList.add(configurer);
            this.configurers.put(clazz, configurerList);
            if (buildState.isInitializing()){
                /**
                 * 如果是在初始化期间, 则加入 {@link #configurersAddedInInitializing}
                 */
                this.configurersAddedInInitializing.addAll(configurerList);
            }
        }
    }

    /**
     * 根据clazz类型(Class clazz)查找指定的配置器, 如果配置器缓存中存在待查找对象, 那么会将其返回, 否则返回空List集合
     * @param clazz
     * @param <C>
     * @return List<C>
     */
    public <C extends SecurityConfigurer<T, B>> List<C> getConfigurers(Class<C> clazz){
        List<C> configs = (List<C>) this.configurers.get(clazz);
        if (configs == null){
            return Collections.emptyList();
        }
        return new ArrayList<>(configs);
    }

    /**
     * 根据clazz类型(Class clazz)删除指定的配置器, 如果配置器缓存中存在待删除对象, 那么会将其删除并返回, 否则返回空List集合
     * 如果{@link #allowConfigurersOfSameType}为true, 那么表示允许存储相同类型的configurer, 删除或获取的返回值需要使用集合类型
     * @param clazz
     * @param <C>
     * @return List<C>
     */
    public <C extends SecurityConfigurer<T, B>> List<C> removeConfigurers(Class<C> clazz){
        List<C> configs = (List<C>) this.configurers.remove(clazz);
        if (configs == null){
            return Collections.emptyList();
        }
        return new ArrayList<>(configs);
    }

    /**
     * 根据clazz类型(Class clazz)查找指定的配置器, 如果配置器缓存中存在待查找对象, 那么会将其返回, 否则返回null
     * 这里返回的只能存在一个元素
     * @param clazz
     * @param <C>
     * @return C
     */
    public <C extends SecurityConfigurer<T, B>> C getConfigurer(Class<C> clazz){
        List<SecurityConfigurer<T, B>> configs = this.configurers.get(clazz);
        if (configs == null){
            return null;
        }
        if (configs.size() != 1){
            throw new IllegalStateException("Only one configurer expected for type " + clazz + ", but got " + configs);
        }
        return (C) configs.get(0);
    }

    /**
     * 根据clazz类型(Class clazz)删除指定的配置器, 如果配置器缓存中存在待删除对象, 那么会将其删除并返回, 否则返回null
     * 这里返回的只能存在一个元素
     * 如果{@link #allowConfigurersOfSameType}为false, 那么表示不允许存储相同类型的configurer, 删除或获取的返回值需要个体元素类型
     * @param clazz
     * @param <C>
     * @return C
     */
    public <C extends SecurityConfigurer<T, B>> C removeConfigurer(Class<C> clazz){
        List<SecurityConfigurer<T, B>> configs = this.configurers.remove(clazz);
        if (configs == null){
            return null;
        }
        if (configs.size() != 1){
            throw new IllegalStateException("Only one configurer expected for type " + clazz + ", but got " + configs);
        }
        return (C) configs.get(0);
    }

    /**
     * 设置后置处理器, 返回当前调用对象
     * @param objectPostProcessor
     * @return
     */
    public T objectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor){
        Assert.notNull(objectPostProcessor, "objectPostProcessor cannot be null");
        this.objectPostProcessor = objectPostProcessor;
        return (T) this;
    }

    /**
     * 后置处理器调用
     * @param object
     * @param <P>
     * @return
     */
    protected <P> P postProcess(P object){
        return this.objectPostProcessor.postProcess(object);
    }

    /**
     * boBuild 方法实现
     * @return
     * @throws Exception
     */
    @Override
    protected T doBuild() throws Exception {

        synchronized (configurers){
            buildState = BuildState.INITIALIZING;

            beforeInit();
            init();

            buildState = BuildState.CONFIGURING;

            beforeConfigure();
            configure();

            buildState = BuildState.BUILDING;

            T result = performBuild();

            buildState = BuildState.BUILT;

            return result;
        }
    }

    /**
     * 在init方法之前调用, 目前是一个空实现方法, 子类可以重写进行一些自定义
     * @throws Exception
     */
    protected void beforeInit() throws Exception {
    }

    /**
     * 在configure方法之前调用, 目前是一个空实现方法, 子类可以重写进行一些自定义
     * @throws Exception
     */
    protected void beforeConfigure() throws Exception {
    }

    /**
     * 构建流程的最后一步, 子类必须实现已完成自己的业务
     * @return
     * @throws Exception
     */
    protected abstract T performBuild() throws Exception;


    /**
     * 执行每一个配置器的init方法
     * @throws Exception
     */
    private void init() throws Exception {
        Collection<SecurityConfigurer<T, B>> configurers =getConfigurers();
        for (SecurityConfigurer<T, B> configurer : configurers){
            configurer.init((B) this);
        }

        for (SecurityConfigurer<T, B> configurer : this.configurersAddedInInitializing){
            configurer.init((B) this);
        }
    }

    /**
     * 执行每一个配置器的configure方法
     * @throws Exception
     */
    private void configure() throws Exception {
        Collection<SecurityConfigurer<T, B>> configurers = getConfigurers();
        for (SecurityConfigurer<T, B> configurer : configurers){
            configurer.configure((B) this);
        }
    }

    /**
     * 获取到所有的配置器集合
     * @return
     */
    private Collection<SecurityConfigurer<T, B>> getConfigurers(){
        List<SecurityConfigurer<T, B>> result = new ArrayList<>();
        for (List<SecurityConfigurer<T, B>> configurers : this.configurers.values()){
            result.addAll(configurers);
        }
        return result;
    }

    private boolean isUnBuild(){
        synchronized (configurers){
            return buildState == BuildState.UN_BUILT;
        }
    }

// inner class

    private static enum BuildState {
        /**
         * This is the state before the {@link SecurityBuilder#build()} is invoked
         * 这是调用{@link SecurityBuilder＃build（）}之前的状态
         */
        UN_BUILT(0),

        /**
         * The state from when {@link SecurityBuilder#build()} is first invoked until all the
         * {@link SecurityConfigurer#init(SecurityBuilder)} methods have been invoked.
         *
         * 从第一次调用{@link SecurityBuilder＃build（）}到所有{@link SecurityConfigurer＃init（SecurityBuilder）}方法都被调用的状态。
         */
        INITIALIZING(1),

        /**
         * The state from after all {@link SecurityConfigurer#init(SecurityBuilder)} have
         * been invoked until after all the
         * {@link SecurityConfigurer#configure(SecurityBuilder)} methods have been
         * invoked.
         *
         * 从所有{@link SecurityConfigurer＃init（SecurityBuilder）}被调用之后到所有{@link SecurityConfigurer＃configure（SecurityBuilder）}方法被调用之后的状态。
         */
        CONFIGURING(2),

        /**
         * From the point after all the
         * {@link SecurityConfigurer#configure(SecurityBuilder)} have completed to just
         * after {@link AbstractConfiguredSecurityBuilder#performBuild()}.
         *
         * 从所有{@link SecurityConfigurer＃configure（SecurityBuilder）}完成之后到{@link AbstractConfiguredSecurityBuilder＃performBuild（）}之后
         */
        BUILDING(3),

        /**
         * After the object has been completely built.
         * 表示对象已经完成了构建
         */
        BUILT(4);

        private final int order;

        BuildState(int order){
            this.order = order;
        }

        public boolean isInitializing() {
            return INITIALIZING.order == order;
        }

        /**
         * Determines if the state is CONFIGURING or later
         *
         * 确定状态是 CONFIGURING 还是 往后版本
         * @return
         */
        public boolean isConfigured() {
            return order >= CONFIGURING.order;
        }

    }
}
