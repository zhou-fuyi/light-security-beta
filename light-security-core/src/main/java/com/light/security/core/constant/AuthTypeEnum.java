package com.light.security.core.constant;

/**
 * @EnumName AuthTypeEnum
 * @Description 权限类型, 记录RBAC模型的构造形式, 目前提供简单、进阶、组概念三种
 * 具体的参见当前路径下 light-security-simple.ddl 和 light-security-advance.ddl
 * @Author ZhouJian
 * @Date 2019-12-09
 */
public enum AuthTypeEnum {

    SIMPLE
    ,ADVANCE
    ,GROUP
    ;

}
