package com.light.security.core.cache.model;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName InternalExpiredValueWrapper
 * @Description 传递内部缓存数据(支持过期时间检测)的包装类
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class InternalExpiredValueWrapper<T> extends AbstractSupportExpired<T> implements InternalContextSupportExpired<T>{

    private final long targetExpired;

    public InternalExpiredValueWrapper(String key, T content) {
        super(key, content);
        this.targetExpired = getExpired();
    }

    public InternalExpiredValueWrapper(String key, T content, long expired) {
        super(key, content, expired);
        this.targetExpired = getExpired();
    }

    public InternalExpiredValueWrapper(String key, T content, TimeUnit unit) {
        super(key, content, unit);
        this.targetExpired = getExpired();
    }

    public InternalExpiredValueWrapper(String key, T content, TimeUnit unit, Long expired) {
        super(key, content, unit, expired);
        this.targetExpired = getExpired();
    }

    public long getTargetExpired() {
        return targetExpired;
    }

    @Override
    public boolean isExpired() {
        return getTargetExpired() <= getCurrentTimeMills();
    }

    @Override
    protected long buildExpiredWithTimeUnit(Long addition) {
        return super.buildExpiredWithTimeUnit(addition) + getCurrentTimeMills();
    }

    /**
     * 获取当前系统时间
     * @return
     */
    private long getCurrentTimeMills(){
        return System.currentTimeMillis();
    }
}
