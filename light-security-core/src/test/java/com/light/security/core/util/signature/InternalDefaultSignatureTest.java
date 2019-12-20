package com.light.security.core.util.signature;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InternalDefaultSignatureTest {

    private InternalDefaultSignature signature = new InternalDefaultSignature();

    @Test
    public void sign() {

        int index = 100;
        while (index > 0){
            System.out.println(signature.sign(null, "zhoujian"));
            System.out.println("-------------------------------------------------------");
            index--;
        }

    }

    @Test
    public void parse(){
        String signature = "emhvdWppYW4sODVjYTRhOTMtNWIyNy00YQ==";//zhoujian,85ca4a93-5b27-4a
        Map<String, Object> result  = this.signature.parse(signature, null);
        result.forEach((key, val) -> System.out.println("key is :" + key + " and the value is :" + val));
        Assert.assertEquals(result.size(), 2);
    }

    @Test
    public void secretEquals(){
        String origin = "emhvdWppYW4sZTcyNzM1NjctYzk2MC00Ng==";
        String now = "emhvdWppYW4sYmM1MzYyMTUtYWYxMy00MA==";
        Assert.assertTrue(signature.signatureEquals(origin, now));
    }

    @Test
    public void testHashMap(){
        HashMap<String, String> testMap = new HashMap<>(2);
        String keyName = "zhoujian";
        testMap.put(keyName, "123");
        testMap.put(keyName, "456");
        testMap.forEach((key, val) -> System.out.println("key: " + key + " and the value :" + val));
    }

    @Test
    public void testConcurrentHashMap(){
        ConcurrentHashMap<String, String> testMap = new ConcurrentHashMap<>(2);
        String keyName = "zhoujian";
        testMap.put(keyName, "123");
        testMap.put(keyName, "456");
        testMap.forEach((key, val) -> System.out.println("key: " + key + " and the value :" + val));
    }

    @Test
    public void testMD5() throws NoSuchAlgorithmException {
        String secret = "zhoujian";
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(secret.getBytes());
        byte[] secretBytes = digest.digest();
        final String result1 = DatatypeConverter.printHexBinary(secretBytes);
        System.out.println("md5 1 : " + result1);
        digest.update(secret.getBytes());
        secretBytes = digest.digest();
        final String result2 = DatatypeConverter.printHexBinary(secretBytes);
        System.out.println("md5 2 : " + result2);
    }
}