package com.light.security.core.config.core.configurer;

import com.light.security.core.config.core.builder.FilterChainBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * @ClassName CorsConfigurer
 * @Description CorsFilter配置器
 * @Author ZhouJian
 * @Date 2019-12-03
 */
public class CorsConfigurer<B extends FilterChainBuilder<B>> extends AbstractHttpConfigurer<CorsConfigurer<B>, B> {


    private static final String HANDLER_MAPPING_INTROSPECTOR = "org.springframework.web.servlet.handler.HandlerMappingIntrospector";

    private static final String CORS_CONFIGURATION_SOURCE_BEAN_NAME = "corsConfigurationSource";

    private static final String CORS_FILTER_BEAN_NAME = "corsFilter";

    private CorsConfigurationSource corsConfigurationSource;

    public CorsConfigurer(){

    }

    public CorsConfigurer<B> configurationSource(CorsConfigurationSource corsConfigurationSource){
        this.corsConfigurationSource = corsConfigurationSource;
        return this;
    }

    @Override
    public void configure(B builder) throws Exception {
        ApplicationContext context = builder.getSharedObject(ApplicationContext.class);

        CorsFilter corsFilter = getCorsFilter(context);
        if (corsFilter == null){
            throw new IllegalArgumentException("Please configure either a " + CORS_FILTER_BEAN_NAME + " bean or a " + CORS_CONFIGURATION_SOURCE_BEAN_NAME + "bean.");
        }
        builder.addFilter(corsFilter);
    }

    private CorsFilter getCorsFilter(ApplicationContext context) {
        if (this.corsConfigurationSource != null){
            return new CorsFilter(corsConfigurationSource);
        }
        boolean containsCorsFilter =  context.containsBeanDefinition(CORS_FILTER_BEAN_NAME);
        if (containsCorsFilter){
            return context.getBean(CORS_FILTER_BEAN_NAME, CorsFilter.class);
        }

        boolean containsCorsSource = context.containsBean(CORS_CONFIGURATION_SOURCE_BEAN_NAME);
        if (containsCorsSource) {
            CorsConfigurationSource configurationSource = context.getBean(CORS_CONFIGURATION_SOURCE_BEAN_NAME, CorsConfigurationSource.class);
            return new CorsFilter(configurationSource);
        }

        boolean mvcPresent = ClassUtils.isPresent(HANDLER_MAPPING_INTROSPECTOR, context.getClassLoader());
        if (mvcPresent){
            return MvcCorsFilter.getMvcCorsFilter();
        }
        return null;
    }

    static class MvcCorsFilter {
        /**
         * This needs to be isolated into a separate class as Spring MVC is an optional
         * dependency and will potentially cause ClassLoading issues
         *
         * 这需要隔离到一个单独的类中，因为Spring MVC是一个可选的依赖项，并且可能会导致ClassLoading问题
         * @return
         */
        private static CorsFilter getMvcCorsFilter() {
            /**
             * 这是一个Spring MVC助手类，用于集合应用所配置的HandlerMapping(url pattern和请求处理handler之间的映射)表
             *
             * 可以作为 CorsConfigurationSource 使用
             */
            HandlerMappingIntrospector mappingIntrospector = new HandlerMappingIntrospector();
            return new CorsFilter(mappingIntrospector);
        }
    }
}
