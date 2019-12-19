package com.light.security.core.util.signature;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName JwtSignature
 * @Description JWT签名实现
 * @Author ZhouJian
 * @Date 2019-12-19
 */
public class JwtSignature implements Signature {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS256;

    /**
     * 签名
     * @param context 签入内容
     * @param secret 签名私钥
     * @return
     */
    @Override
    public String sign(Map<String, Object> context, String secret) {
        Assert.hasLength(secret, "secret必须设置");
        if (CollectionUtils.isEmpty(context)){
            context = new HashMap<>();
        }
        context.put(secret, secret);
        JwtBuilder builder= Jwts.builder()
                .setClaims(context)
                .signWith(ALGORITHM, secret);
        return builder.compact();
    }

    /**
     * 逆运算
     * @param signature 签名
     * @param secret 反签私钥
     * @return
     */
    @Override
    public Map<String, Object> parse(String signature, String secret) {
        Map<String, Object> context = null;
        try {
            context = Jwts.parser().setSigningKey(secret).parseClaimsJws(signature).getBody();
        }catch (Exception e){
            if (logger.isWarnEnabled()){
                logger.warn("签名私钥错误");
            }
        }
        if (context == null){
            context = new HashMap<>(0);
        }
        return context;
    }

    @Override
    public boolean signatureEquals(String leftSignature, String rightSignature) {
        return false;
    }
}
