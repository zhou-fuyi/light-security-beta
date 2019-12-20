package com.light.security.core.util.signature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @ClassName MD5Signature
 * @Description MD5签名实现, 我这里只需要一个简单的签名, 所以不会进行复杂的实现
 * @Author ZhouJian
 * @Date 2019-12-20
 */
public class MD5Signature implements Signature {

    private static final Logger logger = LoggerFactory.getLogger(MD5Signature.class);
    private static MessageDigest DIGEST = null;
    private static final String ALGORITHM = "MD5";

    static {
        try {
            DIGEST = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            if (logger.isErrorEnabled()){
                logger.error("当前环境不支持: {}", ALGORITHM,  e);
            }else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String sign(Map<String, Object> context, String secret) {
        // TODO: 2019-12-20 先不管context, 先进行简单实现
        DIGEST.update(secret.getBytes());
//        byte[] encodeBytes = DIGEST.digest();
        return DatatypeConverter.printHexBinary(DIGEST.digest());
    }

    @Override
    public Map<String, Object> parse(String signature, String secret) {
        throw new UnsupportedOperationException("暂不提供MD5的解密实现");
    }

    @Override
    public boolean signatureEquals(String leftSignature, String rightSignature) {
        return leftSignature.equals(rightSignature);
    }
}
