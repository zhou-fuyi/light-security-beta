package com.light.security.core.config.core;
import com.light.security.core.exception.AlreadyBuiltException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName AbstractSecurityBuilder
 * @Description {@link SecurityBuilder}的第一层默认抽象实现, 用于确保要构建的对象<code> T </code>
 * 仅被构建一次
 * @Author ZhouJian
 * @Date 2019-11-29
 *
 * @param <T> 表示需要被构建的对象类型
 */
public abstract class AbstractSecurityBuilder<T> implements SecurityBuilder<T> {

    private AtomicBoolean building = new AtomicBoolean();

    private T targetObject;

    @Override
    public final T build() throws Exception {
        if (this.building.compareAndSet(false, true)){
            this.targetObject = doBuild();
            return this.targetObject;
        }
        throw new AlreadyBuiltException("该对象已经完成了构造");
    }

    public final T getTarget(){
        //先进行目标对象的构建状态判断, 如果为false则为未构建状态
        if (!this.building.get()){
            throw new IllegalArgumentException("目前对象还没有进行构造");
        }
        return this.targetObject;
    }

    /**
     * 由子类进行实现, build方法会调用此方法
     * @return
     * @throws Exception
     */
    protected abstract T doBuild() throws Exception;
}
