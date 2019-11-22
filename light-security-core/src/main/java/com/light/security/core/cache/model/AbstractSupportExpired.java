package com.light.security.core.cache.model;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName AbstractSupportExpired
 * @Description 支持过期时间的数据包装对象的通用实现
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public abstract class AbstractSupportExpired<T> implements SupportExpired<T>{

    private long additionTime = 60 * 60 * 24 * 15;//单位默认是秒
    private TimeUnit unit = TimeUnit.SECONDS;//决定了expiredTime的单位, 默认是秒
    private T content;//被包装的数据对象
    private final String key;

    /**
     * 使用默认的过期时长
     * @param key
     * @param content
     */
    protected AbstractSupportExpired(String key, T content){
        this(key, content, null, null);
    }

    /**
     * 指定过期时长
     * @param key
     * @param content
     * @param expired
     */
    protected AbstractSupportExpired(String key, T content, long expired){
        this(key, content, null, expired);
    }

    protected AbstractSupportExpired(String key, T content, TimeUnit unit){
        this(key, content, unit, null);
    }

    /**
     * 同时指定过期时间与其单位
     * @param key
     * @param content
     * @param unit
     * @param expired
     */
    protected AbstractSupportExpired(String key, T content, TimeUnit unit, Long expired){
        if (StringUtils.isEmpty(key) || content == null){
            throw new IllegalArgumentException("构造器不接受空值参数 --> key is null or '' ( or content is null)");
        }
        this.key = key;
        this.content = content;
        if (unit != null){
            this.unit = unit;
        }
        if (expired != null){
            this.additionTime = expired;
        }
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return unit;
    }

    @Override
    public long getExpired() {
        return buildExpiredWithTimeUnit(null);
    }

    @Override
    public long getExpired(Long addition) {
        return buildExpiredWithTimeUnit(addition);
    }

    @Override
    public T getContent() {
        return content;
    }

    /**
     * 将传入时间（单位秒）转换为毫秒计算, 这里提供时间的附加值, 如果构造函数中没有指定过期时间, 可以在这里在默认时间上相加
     * @param addition 附加的时间长度
     * @return
     */
    protected long buildExpiredWithTimeUnit(Long addition){
        long targetTime = this.additionTime;
        if (addition != null && addition > 0){
            targetTime += addition;
        }
        return unit.toMillis(targetTime);//转换为毫秒数进行比较
    }
}
