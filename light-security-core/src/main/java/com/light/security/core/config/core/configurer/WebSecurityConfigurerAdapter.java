package com.light.security.core.config.core.configurer;

import com.light.security.core.config.core.WebSecurityConfigurer;
import com.light.security.core.config.core.builder.ChainProxyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * @ClassName WebSecurityConfigurerAdapter
 * @Description 仿照SpringSecurity完成
 * Provides a convenient base class for creating a {@link WebSecurityConfigurer}
 * instance. The implementation allows customization by overriding methods.
 *
 * <p>
 * Will automatically apply the result of looking up
 * {@link AbstractHttpConfigurer} from {@link SpringFactoriesLoader} to allow
 * developers to extend the defaults.
 * To do this, you must create a class that extends AbstractHttpConfigurer and then create a file in the classpath at "META-INF/spring.factories" that looks something like:
 * </p>
 * <pre>
 * org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer = sample.MyClassThatExtendsAbstractHttpConfigurer
 * </pre>
 * If you have multiple classes that should be added you can use "," to separate the values. For example:
 *
 * <pre>
 * org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer = sample.MyClassThatExtendsAbstractHttpConfigurer, sample.OtherThatExtendsAbstractHttpConfigurer
 * </pre>
 *
 *
 * 翻译:
 * 提供用于创建{@link WebSecurityConfigurer}实例的便捷基类。 该实现允许通过覆盖方法进行自定义。
 * 将自动应用从{@link SpringFactoriesLoader}中查找{@link AbstractHttpConfigurer}的结果，以允许
 * 开发人员可以扩展默认值。
 * 为此，您必须创建一个扩展AbstractHttpConfigurer的类，然后在类路径中的“ META-INF / spring.factories”处创建一个文件，该文件如下所示：
 * org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer = sample.MyClassThatExtendsAbstractHttpConfigurer
 *
 * @Author ZhouJian
 * @Date 2019-11-29
 */
@Order(100)
public abstract class WebSecurityConfigurerAdapter implements WebSecurityConfigurer<ChainProxyBuilder> {

    // TODO: 2019-11-29 待完成
    protected final Logger logger = LoggerFactory.getLogger(getClass());
}
