package com.light.security.core.config.core.configurer;

import com.light.security.core.access.AuthenticationProvider;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.dao.DaoAuthenticationProvider;
import com.light.security.core.config.core.builder.AuthenticationManagerBuilder;
import com.light.security.core.util.crypto.password.PasswordEncoder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @ClassName InitializeSubjectDetailBeanManagerConfigurer
 * @Description 仿照SpringSecurity完成
 * Lazily initializes the global authentication with a {@link SubjectDetailService} if it is
 * not yet configured and there is only a single Bean of that type. Optionally, if a
 * {@link PasswordEncoder} is defined will wire this up too.
 *
 *
 * 翻译:
 * 如果尚未配置{@link SubjectDetailService}，并且只有一个该类型的Bean，
 * 则可以使用{@link SubjectDetailService}惰性地初始化全局身份验证。
 * （可选）如果定义了{@link PasswordEncoder}，也将其连接起来。
 *
 *
 * 小声BB:
 * 其实这里和{@link InitializeAuthenticationProviderBeanManagerConfigurer}中的情况差不多, 其中不同的在于,
 * 这里实在{@link AuthenticationManagerBuilder#isConfigured()}不成立的情况下, 给<code> AuthenticationManagerBuilder </code>
 * 的实例尝试设置<code> SubjectDetailService </code>属性, 详细代码中尝试获取<code> SubjectDetailService 和 PasswordEncoder </code>
 * 如果获取成功, 则给其赋值, 并在此创建一个{@link DaoAuthenticationProvider}注入到 <code> AuthenticationManagerBuilder #authenticationProviders</code>属性中
 *
 * @Author ZhouJian
 * @Date 2019-12-03
 */
@Order(InitializeSubjectDetailBeanManagerConfigurer.DEFAULT_ORDER)
public class InitializeSubjectDetailBeanManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {

    static final int DEFAULT_ORDER = Ordered.LOWEST_PRECEDENCE - 5000;

    private ApplicationContext context;

    public InitializeSubjectDetailBeanManagerConfigurer(ApplicationContext context){
        this.context = context;
    }

    @Override
    public void init(AuthenticationManagerBuilder builder) throws Exception {
        builder.apply(new InitializeSubjectDetailManagerConfigurer());
    }

    class InitializeSubjectDetailManagerConfigurer extends GlobalAuthenticationConfigurerAdapter{
        @Override
        public void configure(AuthenticationManagerBuilder builder) throws Exception {
            if (builder.isConfigured()) {
                return;
            }
            SubjectDetailService subjectDetailService = getBeanOrNull(SubjectDetailService.class);
            if (subjectDetailService == null) {
                return;
            }

            PasswordEncoder passwordEncoder = getBeanOrNull(PasswordEncoder.class);

            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setSubjectDetailService(subjectDetailService);
            if (passwordEncoder != null) {
                provider.setPasswordEncoder(passwordEncoder);
            }

            builder.authenticationProvider(provider);
        }

        /**
         * @return
         */
        private <T> T getBeanOrNull(Class<T> type) {
            String[] userDetailsBeanNames = InitializeSubjectDetailBeanManagerConfigurer.this.context
                    .getBeanNamesForType(type);
            /**
             * 如果数量不等于1 返回null
             */
            if (userDetailsBeanNames.length != 1) {
                return null;
            }

            return InitializeSubjectDetailBeanManagerConfigurer.this.context.getBean(userDetailsBeanNames[0], type);
        }
    }
}
