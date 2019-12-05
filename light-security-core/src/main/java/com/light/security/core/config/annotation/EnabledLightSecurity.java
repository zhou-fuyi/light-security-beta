package com.light.security.core.config.annotation;

import com.light.security.core.config.configuration.WebSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @AnnotationName EnabledLightSecurity
 * @Description 启动 Light Security
 * 将次注释添加到{@code @Configuration}标注的类中, 以在任何{@link com.light.security.core.config.core.WebSecurityConfigurer}
 * 中定义Light Security配置, 也可以通过拓展{@link com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter}
 * 并覆盖配置方法来定义
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableWebSecurity
 * public class MyWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
 *
 * 	&#064;Override
 * 	public void configure(WebSecurity web) throws Exception {
 * 		web.ignoring()
 * 		// Spring Security should completely ignore URLs starting with /resources/
 * 				.antMatchers(&quot;/resources/**&quot;);
 * 	}
 *
 * 	&#064;Override
 * 	protected void configure(HttpSecurity http) throws Exception {
 * 		http.authorizeRequests().antMatchers(&quot;/public/**&quot;).permitAll().anyRequest()
 * 				.hasRole(&quot;USER&quot;).and()
 * 				// Possibly more configuration ...
 * 				.formLogin() // enable form based log in
 * 				// set permitAll for all URLs associated with Form Login
 * 				.permitAll();
 * 	}
 *
 * 	&#064;Override
 * 	protected void configure(AuthenticationManagerBuilder auth) {
 * 		auth
 * 		// enable in memory based authentication with a user named &quot;user&quot; and &quot;admin&quot;
 * 		.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;)
 * 				.and().withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;, &quot;ADMIN&quot;);
 * 	}
 *
 * 	// Possibly more overridden methods ...
 * }
 * </pre>
 * @Author ZhouJian
 * @Date 2019-12-04
 */
@Retention(value = RetentionPolicy.RUNTIME)//注解的生命周期
@Target(value = {ElementType.TYPE})//性质和Retention一样，都是注解类的属性，表示注解类应该在什么位置，对那一块的数据有效
@Documented
@Import({WebSecurityConfiguration.class})
@EnableGlobalAuthentication
@Configuration
public @interface EnabledLightSecurity {

}
