package com.light.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class LightSecurityClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(LightSecurityClientApplication.class);
    }
}
