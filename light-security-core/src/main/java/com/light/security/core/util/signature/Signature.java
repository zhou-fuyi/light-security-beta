package com.light.security.core.util.signature;

import java.util.Map;

/**
 * @InterfaceName Signature
 * @Description 签名接口
 * @Author ZhouJian
 * @Date 2019-12-17
 */
public interface Signature {

    /**
     * 签名
     * @param context 签入内容
     * @param secret 签名私钥
     * @return
     */
    String sign(Map<String, Object> context, String secret);

    /**
     * 解析加密数据
     * @param signature 签名
     * @param secret 反签私钥
     * @return
     */
    Map<String, Object> parse(String signature, String secret);

    /**
     * 进行签名对比, 由子类进行实现
     * @param leftSignature 签名1
     * @param rightSignature 签名2
     * @return
     */
    boolean signatureEquals(String leftSignature, String rightSignature);
}
