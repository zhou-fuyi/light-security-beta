package com.light.security.core.access.meta;

import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.access.FilterInvocation;
import com.light.security.core.cache.holder.SecurityMetadataSourceContextCacheHolder;
import com.light.security.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName DefaultFilterInvocationSecurityMetadataSource
 * @Description 仿照SpringSecurity完成
 * Default implementation of <tt>FilterInvocationDefinitionSource</tt>.
 * <p>
 * Stores an ordered map of {@link com.light.security.core.util.matcher.RequestMatcher}s to <tt>ConfigAttribute</tt>
 * collections and provides matching of {@code FilterInvocation}s against the items stored
 * in the map.
 * <p>
 * The order of the {@link com.light.security.core.util.matcher.RequestMatcher}s in the map is very important. The <b>first</b>
 * one which matches the request will be used. Later matchers in the map will not be
 * invoked if a match has already been found. Accordingly, the most specific matchers
 * should be registered first, with the most general matches registered last.
 * <p>
 * The most common method creating an instance is using the Spring Security namespace. For
 * example, the {@code pattern} and {@code access} attributes of the
 * {@code <intercept-url>} elements defined as children of the {@code <http>}
 * element are combined to build the instance used by the
 * {@code FilterSecurityInterceptor}.
 *
 *
 * 翻译:
 * <tt> FilterInvocationDefinitionSource </ tt>的默认实现。
 * <p>
 * 将{@link com.light.security.core.util.matcher.RequestMatcher}的有序映射存储到
 * <tt> ConfigAttribute </ tt>集合，并提供{@code FilterInvocation}与映射中存储的项的匹配。
 * <p>
 * Map中{@link com.light.security.core.util.matcher.RequestMatcher}的顺序非常重要。
 * 将使用与请求匹配的<b> first </ b>。 如果已经找到匹配项，则Map中的后续匹配器将不会被调用。
 * 因此，最具体的匹配项应首先注册，最一般的匹配项最后注册。
 * <p>
 * 创建实例的最常见方法是使用Spring Security名称空间。 例如，将被定义为{@code <http>}元素的
 * 子元素的{@code <intercept-url>}元素的{@code pattern}和{@code access}属性进行组合，
 * 以构建由 {@code FilterSecurityInterceptor}
 *
 *
 * 小声BB:
 * <code>SecurityMetadataSource</code>用于取得权限的元数据，并将其使用RequestMatch作为key放入
 * 一个Map集合中，这里所指权限元数据，应该是系统启动时加载的所有用于权限校验时的元数据，可以是数据库中
 * 所有的API权限数据或者其他具有可拦截方式的权限数据
 *
 * 当一个request上来时，便是从元数据中查找当前请求需要的权限，然后获取到权限之后便于账户持有权限进行比对，
 * 简单实现便是：如果账户持有权限便赋予访问权限，否则视为无权操作
 * @Author ZhouJian
 * @Date 2019-11-28
 */
public class DefaultFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * SecurityMetadataSource数据的操作封装
     */
    private SecurityMetadataSourceContextCacheHolder securityMetadataSourceHolder;

    public DefaultFilterInvocationSecurityMetadataSource(SecurityMetadataSourceContextCacheHolder securityMetadataSourceHolder){
        Assert.notNull(securityMetadataSourceHolder, "构造器不接受空值参数 --> securityMetadataSourceHolder is null");
        this.securityMetadataSourceHolder = securityMetadataSourceHolder;
    }

    /**
     * 根据预定义，本类只支持FilterInvocation作为object的格式
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : securityMetadataSourceHolder.getCache().getContext().entrySet()){
            if (entry.getKey().matches(request)){
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : securityMetadataSourceHolder.getCache().getContext().entrySet()){
            allAttributes.addAll(entry.getValue());
        }
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
