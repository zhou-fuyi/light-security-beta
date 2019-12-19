package com.light.security.core.util.signature;

import io.jsonwebtoken.impl.DefaultJwtBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @ClassName InternalDefaultSignature
 * @Description 根据随机字符结合传入内容进行签名
 *
 * 应用场景: 签名用于内部容器key的取值, 同时可以作为 session-cookie模式下token取值,
 * 私钥必须要保证其唯一性
 * @Author ZhouJian
 * @Date 2019-12-17
 */
public class InternalDefaultSignature extends DefaultJwtBuilder implements Signature{

    private static final Logger logger = LoggerFactory.getLogger(InternalDefaultSignature.class);
    private final static String DEFAULT_DELIMITER = ",";
    private final static int END_INDEX = 16;
    private final static String SECRET_KEY = "secret";
    private final static String RANDOM_KEY = "random";

    static {
        if (logger.isWarnEnabled()){
            logger.warn("私钥(secret)必须保证唯一性, 且为非敏感数据, 切记啊...");
        }
    }

    public InternalDefaultSignature(){}

    private String delimiter = DEFAULT_DELIMITER;
    private String secretKey = SECRET_KEY;
    private String randomKey = RANDOM_KEY;
    private int endIndex = END_INDEX;

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public String sign(Map<String, Object> context, String secret) {
        Assert.hasLength(secret, "secret必须设置");
        StringBuffer buffer = new StringBuffer(secret);
        buffer.append(delimiter);
        buffer.append(UUID.randomUUID().toString(), 0, endIndex);
        if (logger.isDebugEnabled()){
            logger.debug("buffer is : {}", buffer.toString());
        }
        return Base64.getEncoder().encodeToString(buffer.toString().getBytes());
    }

    /**
     * 逆运算, 这里没有用到私钥, 因为实现很简单呐
     * @param signature 签名
     * @param secret 反签私钥
     * @return
     */
    @Override
    public Map<String, Object> parse(String signature, String secret) {
        Assert.hasLength(signature, "signature 必须有值");
        Map<String, Object> context = new HashMap<>(2);
        String decodeSignature = new String(Base64.getDecoder().decode(signature));
        String[] splitByDelimiter = decodeSignature.split(delimiter);

        if (logger.isDebugEnabled()){
            logger.debug("拆分后数组为: {}", Arrays.asList(splitByDelimiter));
        }
        if (splitByDelimiter.length < 2){
            throw new IllegalArgumentException("传入签名格式不正确, 无法解析");
        }
        context.put(secretKey, splitByDelimiter[0]);
        context.put(randomKey, splitByDelimiter[1]);
        return context;
    }

    /**
     * 进行签名对比, 这里对比的依据是根据私钥进行比对, 如果私钥相同则相同
     * @param leftSignature 签名
     * @param rightSignature 私钥
     * @return 如果签名解析出来的内容中的私钥部分相同, 则比对成功, 否则失败
     */
    public boolean signatureEquals(String leftSignature, final String rightSignature){
        final String parseLeftSecret = (String) parse(leftSignature, null).get(secretKey);
        final String parseRightSecret = (String) parse(rightSignature, null).get(secretKey);

        if (parseLeftSecret == null || parseRightSecret == null){
            return false;
        }
        return parseLeftSecret.equals(parseRightSecret);
    }

}
