package com.light.security.core.config.configuration;

import com.light.security.core.config.core.AutowireBeanFactoryObjectPostProcessor;
import com.light.security.core.config.core.ObjectPostProcessor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ObjectPostProcessorConfiguration
 * @Description 仿照SpringSecurity完成
 * Spring {@link Configuration} that exports the default {@link ObjectPostProcessor}. This
 * class is not intended to be imported manually rather it is imported automatically when
 * using {@link com.light.security.core.config.annotation.EnabledLightSecurity}.
 *
 *
 * 翻译:
 * Spring {@link Configuration}导出默认的{@link ObjectPostProcessor}。
 * 此类并非旨在手动导入，而是在使用{@link com.light.security.core.config.annotation.EnabledLightSecurity}时自动导入。
 *
 * @Author ZhouJian
 * @Date 2019-12-03
 */
@Configuration
public class ObjectPostProcessorConfiguration {

    @Bean
    public ObjectPostProcessor<Object> objectObjectPostProcessor(AutowireCapableBeanFactory beanFactory){
        return new AutowireBeanFactoryObjectPostProcessor(beanFactory);
    }

}
