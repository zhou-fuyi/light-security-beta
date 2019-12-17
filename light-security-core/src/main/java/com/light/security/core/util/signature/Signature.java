package com.light.security.core.util.signature;

/**
 * @InterfaceName Signature
 * @Description 签名接口
 * @Author ZhouJian
 * @Date 2019-12-17
 */
public interface Signature {

    /**
     * 接受一个入参进行签名, 存在可逆与不可逆两种情况
     * @param context
     * @return
     */
    String sign(Object context);

}
