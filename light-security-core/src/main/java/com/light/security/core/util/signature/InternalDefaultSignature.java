package com.light.security.core.util.signature;

import org.springframework.util.Assert;
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
        Assert.notNull(context, "传入参数不能为 null");
        StringBuffer buffer = new StringBuffer(context.toString());
        buffer.append(LIMIT_SYMBOL);
        buffer.append(UUID.randomUUID());
        return buffer.toString();
    }
}
