package com.light.security.client.controller;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.cache.holder.AuthenticatedContextCacheHolder;
import com.light.security.core.cache.model.InternalExpiredValueWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName ContextController
 * @Description 缓存测试接口
 * @Author ZhouJian
 * @Date 2019-12-18
 */
@RequestMapping("/api/v1/context")
@RestController
public class ContextController {

    @Autowired
    private AuthenticatedContextCacheHolder authenticatedContextCacheHolder;

    @GetMapping("/authenticated")
    public Map<String, InternalExpiredValueWrapper<Authentication>> authenticatedMap(){
        return authenticatedContextCacheHolder.getCache().getContext();
    }

}
