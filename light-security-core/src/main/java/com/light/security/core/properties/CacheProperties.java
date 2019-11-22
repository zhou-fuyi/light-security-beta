package com.light.security.core.properties;

/**
 * @ClassName CacheProperties
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class CacheProperties {

    //缓存类型, 目前提供: INTERNAL_CONTEXT 下一步准备支持REDIS
    private Enum CACHE_TYPE;
    private long expire_time;

    public CacheProperties() {
    }

    public Enum getCACHE_TYPE() {
        return CACHE_TYPE;
    }

    public void setCACHE_TYPE(Enum CACHE_TYPE) {
        this.CACHE_TYPE = CACHE_TYPE;
    }

    public long getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(long expire_time) {
        this.expire_time = expire_time;
    }
}
