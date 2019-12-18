package com.light.security.core.config.core.configurer;

import com.light.security.core.config.annotation.EnableGlobalAuthentication;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.builder.AuthenticationManagerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @ClassName EnableGlobalAuthenticationAutowiredConfigurer
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class EnableGlobalAuthenticationAutowiredConfigurer extends GlobalAuthenticationConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ApplicationContext context;

    public EnableGlobalAuthenticationAutowiredConfigurer(ApplicationContext context, ObjectPostProcessor<Object> objectPostProcessor){
        super(objectPostProcessor);
        this.context = context;
    }

    @Override
    public void init(AuthenticationManagerBuilder builder) throws Exception {
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(EnableGlobalAuthentication.class);
        if (logger.isDebugEnabled()){
            logger.debug("Eagerly initializing {}", beansWithAnnotation);
        }
    }
}
