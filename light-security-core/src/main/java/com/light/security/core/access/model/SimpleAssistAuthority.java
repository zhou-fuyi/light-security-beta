package com.light.security.core.access.model;

/**
 * @ClassName AssistAuthority
 * @Description 用于 authority 不直接存储权限数据时作为辅助使用
 * @Author ZhouJian
 * @Date 2019-12-11
 */
public class SimpleAssistAuthority extends AbstractAuthority implements AssistAuthority{

    public SimpleAssistAuthority(Integer id, String type){
        setId(id);
        setType(type);
    }

}
