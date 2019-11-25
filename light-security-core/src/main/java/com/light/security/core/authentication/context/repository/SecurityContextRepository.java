package com.light.security.core.authentication.context.repository;

import com.light.security.core.authentication.context.InternalSecurityContext;
import com.light.security.core.authentication.context.SecurityContext;
import com.light.security.core.authentication.context.holder.HttpRequestResponseHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @InterfaceName SecurityContextRepository
 * @Description 用于在请求之间存储<code>SecurityContext</code>
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public interface SecurityContextRepository {

    /**
     * 获取当前请求对应的SecurityContext, 对于未经身份验证的用户应该返回一个空的SecurityContext, 不允许返回null
     * @param requestResponseHolder
     * @return 返回适用于当前请求的SecurityContext, 绝对不允许返回null
     */
    SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder);

    /**
     * 在一次请求完成时存储SecurityContext
     * @param context 是从context的持有者(holder)中获取到的非null数据
     * @param request 当前请求
     * @param response 当前请求的响应
     */
    void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response);

    /**
     * 查询SecurityContext的存储容器中是否已经包含了适用于当前请求的SecurityContext
     * @param request
     * @return true 表示找到了SecurityContext, 已经包含; false 表示未包含
     */
    boolean containsContext(HttpServletRequest request);
}
