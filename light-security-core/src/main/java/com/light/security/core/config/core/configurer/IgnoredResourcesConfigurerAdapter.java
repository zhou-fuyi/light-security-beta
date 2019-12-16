package com.light.security.core.config.core.configurer;

import com.light.security.core.config.core.IgnoredResourcesConfigurer;
import com.light.security.core.config.core.WebSecurityConfigurer;
import com.light.security.core.config.core.builder.ChainProxyBuilder;
import com.light.security.core.util.matcher.OrRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName IgnoredResourcesConfigurerAdapter
 * @Description 忽略资源适配器, 也就是用于用户配置公共资源的拓展适配器, 这里托管了{@link IgnoredResourcesConfigurer}
 * 接口的实例对象, 资源忽略的配置还是在{@link IgnoredResourcesConfigurer}的具体实现类中实现, 这里只是负责注册
 * @Author ZhouJian
 * @Date 2019-12-16
 */
@Order(200)
public abstract class IgnoredResourcesConfigurerAdapter implements WebSecurityConfigurer<ChainProxyBuilder> {

    /**
     * 依赖查找, {@link IgnoredResourcesConfigurer}的子类需要使用{@link org.springframework.context.annotation.Configuration}注解
     * 标注, 便于被发现
     */
    @Autowired
    private List<IgnoredResourcesConfigurer> ignoredResourcesConfigurers;

    private List<RequestMatcher> ignoredResourceRequests = new ArrayList<>();

    @Override
    public void init(ChainProxyBuilder builder) throws Exception {
        if (!CollectionUtils.isEmpty(ignoredResourcesConfigurers)){
            for (IgnoredResourcesConfigurer ignoredResourcesConfigurer : ignoredResourcesConfigurers){
                ignoredResourcesConfigurer.init();
            }
        }
    }

    @Override
    public void configure(ChainProxyBuilder builder) throws Exception {
        if (!CollectionUtils.isEmpty(ignoredResourcesConfigurers)){

            IgnoredResourceRegistry ignoredResourceRegistry = new IgnoredResourceRegistry(builder.ignoredRequestRegistry());
            for (IgnoredResourcesConfigurer ignoredResourcesConfigurer : ignoredResourcesConfigurers){
                ignoredResourcesConfigurer.ignoredRegistry(ignoredResourceRegistry);
            }
            /**
             * 进行预处理
             */
            preConfigure(ignoredResourceRequests);

            /**
             * 注入到{@link ChainProxyBuilder}中
             */
            if (!CollectionUtils.isEmpty(ignoredResourceRequests)){
                builder.ignoredRequestRegistry().requestMatchers(new OrRequestMatcher(ignoredResourceRequests));
            }
        }
    }

    /**
     * 生命周期函数, 可以在配置前进行一些预处理
     * @param ignoredResourceRequests
     */
    protected void preConfigure(List<RequestMatcher> ignoredResourceRequests){
    }



    public class IgnoredResourceRegistry {

        IgnoredResourceRegistry(ChainProxyBuilder.IgnoredRequestRegistry ignoredRequestRegistry){
            this.ignoredRequestRegistry = ignoredRequestRegistry;
        }

        private ChainProxyBuilder.IgnoredRequestRegistry ignoredRequestRegistry;

        public IgnoredResourceRegistry requestMatcher(RequestMatcher... requestMatchers){
            return chainRequestMatchers(Arrays.asList(requestMatchers));
        }

        protected IgnoredResourceRegistry chainRequestMatchers(List<RequestMatcher> requestMatchers){
            IgnoredResourcesConfigurerAdapter.this.ignoredResourceRequests.addAll(new ArrayList<>(requestMatchers));
            return this;
        }

        // TODO: 2019-12-16 到这里了

        /**
         * 检查资源是否重复, 避免重复注册
         * @param requestMatchers
         * @return
         */
        protected List<RequestMatcher> preChainRequestMatchers(RequestMatcher... requestMatchers){
            return null;
        }

    }
}
