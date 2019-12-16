package com.light.security.core.util.crypto.bcrypt;

import com.light.security.core.util.crypto.password.PasswordEncoder;
import org.junit.Test;

import static org.junit.Assert.*;

public class BCryptPasswordEncoderTest {

    private String password = "123456";

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void encode() {
        for (int i = 0; i < 5; i++){
            System.out.println(passwordEncoder.encode(password));
        }
    }

    @Test
    public void matches() {
        String encodedPass1 = "$2a$10$lLXXPtZeeRALUerQaw8XHOZGQSRMF5OUNdMiKZm0pZgyiZakXtdb6";
        String encodedPass2 = "$2a$10$lLXXPtZeeRALUerQaw8XHOZGQSRMF5OUNdMiKZm0pZgyiZakXtdb6";
        String encodedPass3 = "$2a$10$lLXXPtZeeRALUerQaw8XHOZGQSRMF5OUNdMiKZm0pZgyiZakXtdb6";
        String encodedPass4 = "$2a$10$lLXXPtZeeRALUerQaw8XHOZGQSRMF5OUNdMiKZm0pZgyiZakXtdb6";
        String encodedPass5 = "$2a$10$lLXXPtZeeRALUerQaw8XHOZGQSRMF5OUNdMiKZm0pZgyiZakXtdb6";
        String[] encodes = new String[]{ encodedPass1, encodedPass2, encodedPass3, encodedPass4, encodedPass5 };
        for (int i = 0; i < 5; i++){
            System.out.println(passwordEncoder.matches(password, encodes[i]));
        }
    }
}