package com.light.security.core.util.signature;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @ClassName InternalDefaultSignature
 * @Description 根据随机字符结合传入内容进行签名
 * @Author ZhouJian
 * @Date 2019-12-17
 */
public class InternalDefaultSignature implements Signature{

    private static final String LIMIT_SYMBOL = ".";

    @Override
    public String sign(Object context) {
        StringBuffer buffer = new StringBuffer(context == null ? "" : context.toString());
        if (!StringUtils.isEmpty(buffer.toString())){
            buffer.append(LIMIT_SYMBOL);
        }
        buffer.append(UUID.randomUUID());
        return buffer.toString();
    }
}
