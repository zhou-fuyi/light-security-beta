package com.light.security.core.cache.model;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisExpiredValueWrapper
 * @Description 针对redis设计的数据包装类
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class RedisExpiredValueWrapper<T> extends AbstractSupportExpired<T> implements RedisSupportExpired<T> {

    private static RedisDataTypeEnum DATA_TYPE = RedisDataTypeEnum.LIST;
    private String dataType = DATA_TYPE.getDesc();

    public RedisExpiredValueWrapper(String key, T content, RedisDataTypeEnum dataType) {
        this(key, content, dataType, null, null);
    }

    public RedisExpiredValueWrapper(String key, T content, RedisDataTypeEnum dataType, long expired) {
        this(key, content, dataType, null, expired);
    }

    public RedisExpiredValueWrapper(String key, T content, RedisDataTypeEnum dataType, TimeUnit unit) {
        this(key, content, dataType, unit, null);
    }

    public RedisExpiredValueWrapper(String key, T content, RedisDataTypeEnum dataType, TimeUnit unit, Long expired) {
        super(key, content, unit, expired);
        if (dataType == null){
            dataType = DATA_TYPE;
        }
        this.dataType = dataType.getDesc();
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public String getDataType() {
        return dataType;
    }
}
