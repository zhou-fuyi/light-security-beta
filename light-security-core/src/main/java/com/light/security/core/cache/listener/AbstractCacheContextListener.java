package com.light.security.core.cache.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @ClassName AbstractCacheContextListener
 * @Description <code>ServletContextListener</code>抽象实现
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public abstract class AbstractCacheContextListener implements ServletContextListener {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (logger.isDebugEnabled()){
            logger.debug("启动 ServletContextListener");
        }
        loadCache(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (logger.isDebugEnabled()){
            logger.debug("Servlet 容器销毁");
        }
    }

    /**
     * 加载缓存
     * @param servletContextEvent
     */
    protected abstract void loadCache(ServletContextEvent servletContextEvent);
}
