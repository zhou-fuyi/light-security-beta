package com.light.security.core.config.core.configurer;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.config.core.builder.AuthenticationManagerBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;

/**
 * @ClassName InitializeAuthenticationProviderBeanManagerConfigurer
 * @Description 仿照SpringSecurity完成
 *
 * 主要是进行{@link com.light.security.core.authentication.AuthenticationManager}构建中未给
 * {@link AuthenticationManagerBuilder #parentAuthenticationManager}和{@link AuthenticationManagerBuilder #authenticationProviders}
 * 的其中一个定义值的处理(上面两者必须有一种可用)
 *
 * 这里会尝试到{@link ApplicationContext}中进行寻找, 如果存在, 则赋予获取到的值
 *
 *
 * 小声BB:
 * 其实就是检查一下{@link AuthenticationManagerBuilder#isConfigured()}, 如果返回false, 那么表示会 AuthenticationManager
 * 会创建失败, 因为<code> parentAuthenticationManager </code>和<code> AuthenticationProviders </code>同时不可用, 如果存在这样的情况, 此处会进行一个默认处理, 会
 * 去{@link ApplicationContext}查找 {@link AuthenticationProvider}类型的实例, 如果成功找到, 那么将其注入到 <code> AuthenticationProviders </code>中
 * @Author ZhouJian
 * @Date 2019-12-03
 */
@Order(InitializeAuthenticationProviderBeanManagerConfigurer.DEFAULT_ORDER)
public class InitializeAuthenticationProviderBeanManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {

    static final int DEFAULT_ORDER = InitializeSubjectDetailBeanManagerConfigurer.DEFAULT_ORDER - 100;

    private final ApplicationContext context;

    public InitializeAuthenticationProviderBeanManagerConfigurer(ApplicationContext context){
        this.context = context;
    }

    @Override
    public void init(AuthenticationManagerBuilder builder) throws Exception {
        builder.apply(new InitializeAuthenticationProviderManagerConfigurer());
    }

    class InitializeAuthenticationProviderManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {
        @Override
        public void configure(AuthenticationManagerBuilder builder) throws Exception {
            if (builder.isConfigured()) {
                return;
            }
            AuthenticationProvider authenticationProvider = getBeanOrNull(AuthenticationProvider.class);
            if (authenticationProvider == null) {
                return;
            }
            builder.authenticationProvider(authenticationProvider);
        }

        /**
         * @return
         */
        private <T> T getBeanOrNull(Class<T> type) {
            String[] userDetailsBeanNames = InitializeAuthenticationProviderBeanManagerConfigurer.this.context.getBeanNamesForType(type);
            if (userDetailsBeanNames.length != 1) {
                return null;
            }

            return InitializeAuthenticationProviderBeanManagerConfigurer.this.context.getBean(userDetailsBeanNames[0], type);
        }
    }

}
