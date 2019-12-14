package com.light.security.client.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/test")
@RestController
public class ForTestController {

    @RequestMapping("/me")
    public String me(){
        return "zhouijan";
    }

}
