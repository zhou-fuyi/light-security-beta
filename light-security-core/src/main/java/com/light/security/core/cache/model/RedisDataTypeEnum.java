package com.light.security.core.cache.model;

/**
 * @EnumName RedisDataTypeEnum
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public enum RedisDataTypeEnum {

    STRING("String")
    ,LIST("List")
    ,HASH_SET("HashSet")
    ;

    private String desc;

    RedisDataTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
