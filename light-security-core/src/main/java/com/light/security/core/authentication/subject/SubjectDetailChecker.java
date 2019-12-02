package com.light.security.core.authentication.subject;

/**
 * @InterfaceName SubjectDetailChecker
 * @Description 账户检查, 该接口只进行账户主体关联的持久层数据的检查, 不进行认证相关的检查
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public interface SubjectDetailChecker {

    /**
     * 账户检查
     * @param detail
     */
    void check(SubjectDetail detail);
}
