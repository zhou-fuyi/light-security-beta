package com.light.security.core.config.annotation;

import com.light.security.core.config.configuration.AuthenticationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @AnnotationName EnableGlobalAuthentication
 * @Description 仿照SpringSecurity完成
 * @Author ZhouJian
 * @Date 2019-12-03
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import(AuthenticationConfiguration.class)
@Configuration
public @interface EnableGlobalAuthentication {
}
