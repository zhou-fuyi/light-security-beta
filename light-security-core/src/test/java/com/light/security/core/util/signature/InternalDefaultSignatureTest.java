package com.light.security.core.util.signature;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

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
        Assert.assertTrue(signature.secretEquals(origin, now));
    }
}