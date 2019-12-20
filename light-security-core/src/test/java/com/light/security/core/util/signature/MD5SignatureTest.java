package com.light.security.core.util.signature;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class MD5SignatureTest {

    private MD5Signature signature = new MD5Signature();
    private String secret = "ZhouJian";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void sign() {
        logger.info(signature.sign(null, secret));
        logger.info(signature.sign(null, secret));
    }

    @Test
    public void signatureEquals() {
        final String result1 = signature.sign(null, secret);
        final String result2 = signature.sign(null, secret);
        Assert.assertTrue(result1.equals(result2));
    }
}