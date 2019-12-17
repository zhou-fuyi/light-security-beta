package com.light.security.core.util.signature;

import org.junit.Test;

public class InternalDefaultSignatureTest {

    private InternalDefaultSignature signature = new InternalDefaultSignature();

    @Test
    public void sign() {

        int index = 100;
        while (index > 0){
            System.out.println(signature.sign("zhoujian"));
            System.out.println("-------------------------------------------------------");
            index--;
        }

    }
}