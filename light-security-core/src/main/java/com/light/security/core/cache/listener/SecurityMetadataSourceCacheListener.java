package com.light.security.core.cache.listener;

import com.light.security.core.access.AuthorityAttribute;
import com.light.security.core.access.ConfigAttribute;
import com.light.security.core.access.authority.GrantedAuthority;
import com.light.security.core.access.meta.DefaultFilterInvocationSecurityMetadataSource;
import com.light.security.core.access.model.ActionAuthority;
import com.light.security.core.access.model.Authority;
import com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessor;
import com.light.security.core.authentication.dao.jdbc.JdbcDaoProcessorManager;
import com.light.security.core.authentication.dao.jdbc.JdbcQuery;
import com.light.security.core.cache.holder.SecurityMetadataSourceContextCacheHolder;
import com.light.security.core.config.core.ObjectPostProcessor;
import com.light.security.core.config.core.SecurityBuilder;
import com.light.security.core.config.core.WebSecurityConfigurer;
import com.light.security.core.config.core.builder.ChainProxyBuilder;
import com.light.security.core.config.core.configurer.WebSecurityConfigurerAdapter;
import com.light.security.core.exception.ProcessorNotFoundException;
import com.light.security.core.properties.SecurityProperties;
import com.light.security.core.util.matcher.AntPathRequestMatcher;
import com.light.security.core.util.matcher.AnyRequestMatcher;
import com.light.security.core.util.matcher.OrRequestMatcher;
import com.light.security.core.util.matcher.RequestMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.util.*;

/**
 * @ClassName SecurityMetadataSourceCacheListener
 * @Description 用于加载<code>SecurityMetadataSource</code>, 并将其存放于<code>SecurityMetadataSourceCache</code>中
 * @Author ZhouJian
 * @Date 2019-11-28
 */
@WebListener
public class SecurityMetadataSourceCacheListener extends AbstractCacheContextListener {

    private final double AUTHORITY_OPENED_THRESHOLD = 0.5d;

    /**
     * 这里注入主要是如果存在启动创建表的情况, 那么必须要在{@link SecurityMetadataSourceCacheListener}之前执行
     *
     * 在这里进行注入, 那么就会在进行{@link SecurityMetadataSourceCacheListener}属性装配的时候初始化相关类
     */
    @Autowired
    private JdbcDaoProcessorManager jdbcDaoProcessorManager;

    @Autowired
    private SecurityMetadataSourceContextCacheHolder securityMetadataSourceContextCacheHolder;

    @Autowired
    private List<JdbcDaoProcessor> jdbcDaoProcessors;

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 依赖查找时加载, 避免使用时未初始化导致SQL为空异常
     * 引入原因同{@link JdbcDaoProcessorManager}
     */
    @Autowired
    private JdbcQuery jdbcQuery;

    @Autowired
    private ObjectPostProcessor<Object> postProcessor;

    @Autowired
    private ConfigurableBeanFactory beanFactory;

    @Override
    protected void loadCache(ServletContextEvent servletContextEvent) throws Exception {
        Enum currentSecurityMode = securityProperties.getAuthType();
        Collection<Authority> securityMetadata = null;
        Exception lastException = null;
        for (JdbcDaoProcessor processor : jdbcDaoProcessors){
            if (processor.support(currentSecurityMode)){
                try {
                    securityMetadata = processor.loadSecurityMetadataOnStartup();
                } catch (Exception e) {
                    lastException = e;
                }
                break;
            }
        }
        if (securityMetadata != null){
            transformToSecurityMetadataSources(securityMetadata);
            return;
        }
        if (lastException == null){
            lastException = new ProcessorNotFoundException(500, "未找到适配当前权限模式(" + currentSecurityMode.name() + ")的 JdbcDaoProcessor处理器");
        }
        throw lastException;
    }

    /**
     * 将可用的并且非公共权限转换为特定格式, 并加入到权限数据源中
     * @param authorities
     */
    private void transformToSecurityMetadataSources(Collection<Authority> authorities){
        authorities = preHandler(authorities);
        if (!CollectionUtils.isEmpty(authorities)){
            authorities.forEach(authority -> {
                securityMetadataSourceContextCacheHolder.put(getRequestMatcher(authority), new ArrayList<>(Arrays.asList(new AuthorityAttribute(authority.getAuthorityPoint()))));
            });
        }
        // TODO: 2019/12/14 魔法值待优化
        securityMetadataSourceContextCacheHolder.put(AnyRequestMatcher.INSTANCE, new ArrayList<>(Arrays.asList(new AuthorityAttribute("_authenticated"))));
        logger.info("SecurityMetadataContext is {}",securityMetadataSourceContextCacheHolder.getCache().getContext());
        securityMetadataSourceContextCacheHolder.getCache().getContext().forEach((key, value) -> {
            logger.debug("key is {} and the value is {}", key, value.iterator().next());
        });
    }

    /**
     * 预处理, 主要是进行 enabled 和 opened的处理, 这里会将enabled标识为不可用(enabled = 0)的直接删除,
     * 将opened标识为开放(opened = 1)的另行处理(注册为开放资源).
     * @param authorities
     * @return
     */
    private Collection<Authority> preHandler(Collection<Authority> authorities){
        if (authorities.size() == 0){
            if (logger.isWarnEnabled()){
                logger.warn("当前数据库中权限数据为空, 请尽快填写权限（action权限）数据, 避免后续授权过程引发异常");
            }
            return authorities;
        }
        Collection<Authority> openedAuthorities = new ArrayList<>();
        final int authoritiesSize = authorities.size();
        Iterator<Authority> iterator = authorities.iterator();
        while (iterator.hasNext()){
            Authority authority = iterator.next();
            if (authority.isEnabled() && authority.isOpened()){
                openedAuthorities.add(authority);
                iterator.remove();
            }
            /**
             * 如果权限不可用(enabled = 0), 那就去除
             */
            if (!authority.isEnabled()){
                iterator.remove();
            }
        }
        /**
         * 将opened标识为公共的(opened = 1)权限注册为公共资源
         */
        ignoredOpenedAuthoritiesSecurityConfigurerAdapter(openedAuthorities);

        if (CollectionUtils.isEmpty(authorities)){
            throw new IllegalArgumentException("当前已无可用权限数据, 请检查数据库中权限表数据列 enabled 与 opened 是否设置合适");
        }
        preCheck(authoritiesSize, openedAuthorities.size());
        return authorities;
    }

    /**
     * 前置检查, 在{@link #preHandler(Collection)}最后进行调用, 主要是对进行了预处理的数据进行一些前置检查
     * 其实应该声明几个生命周期函数, 这样方便子类进行切入, 不过目前没有进行实现, 放在后面改善吧
     * @param authoritiesSize
     * @param openedAuthoritiesSize
     */
    private void preCheck(int authoritiesSize, int openedAuthoritiesSize){
        if (openedAuthoritiesSize/ (authoritiesSize * 1.0) >= AUTHORITY_OPENED_THRESHOLD){
            if (logger.isWarnEnabled()){
                logger.warn("当前获取权限数据共有{}则, 开放权限共有{}则, 开放权限占比为{}, 请注意是否开放权限设置过多"
                        , authoritiesSize, openedAuthoritiesSize, (openedAuthoritiesSize/ (authoritiesSize * 1.0)));
            }
        }
    }

    /**
     * 是对传入权限进行包装, 返回一个{@link RequestMatcher}对象
     * @param authority
     * @return
     */
    private RequestMatcher getRequestMatcher(Authority authority){
        ActionAuthority actionAuthority = (ActionAuthority) authority;
        RequestMatcher requestMatcher = new AntPathRequestMatcher(actionAuthority.getPattern());
        return requestMatcher;
    }

    /**
     * 将开放权限注册到{@link ChainProxyBuilder #ignoredRequests}中
     * {@link com.light.security.core.config.configuration.WebSecurityConfiguration#setChainProxyBuilderConfigurers(ObjectPostProcessor, List)}
     * 方法中会在IOC容器中收集{@link WebSecurityConfigurer}接口的实例, 并在合适的实际先后调用其init和configure方法(用于辅助目标对象的构建)
     * @param openedAuthorities
     */
    private void ignoredOpenedAuthoritiesSecurityConfigurerAdapter(Collection<Authority> openedAuthorities){

        if (!CollectionUtils.isEmpty(openedAuthorities)){
            WebSecurityConfigurer ignoredOpenedAuthoritiesSecurityConfigurerAdapter = new WebSecurityConfigurer<ChainProxyBuilder>() {

                @Override
                public void init(ChainProxyBuilder builder) throws Exception {
                    if (logger.isDebugEnabled()){
                        logger.debug("当前开放API权限共有{}则", openedAuthorities.size());
                        openedAuthorities.forEach(openedAuthority -> logger.debug("开放API权限: {}", openedAuthority));
                    }
                }

                @Override
                public void configure(ChainProxyBuilder builder) throws Exception {
                    if (!CollectionUtils.isEmpty(openedAuthorities)){
                        List<RequestMatcher> ignoredMatchers = new ArrayList<>(openedAuthorities.size());
                        for (Authority openedAuthority : openedAuthorities){
                            ignoredMatchers.add(new AntPathRequestMatcher(((ActionAuthority) openedAuthority).getPattern()));
                        }
                        if (!ignoredMatchers.isEmpty()){
                            builder.ignoredRequestRegistry().requestMatchers(new OrRequestMatcher(ignoredMatchers));
                        }
                    }
                }
            };
            logger.debug("target class name : {}", ignoredOpenedAuthoritiesSecurityConfigurerAdapter.getClass().getName());
            beanFactory.registerSingleton(ignoredOpenedAuthoritiesSecurityConfigurerAdapter.getClass().getName(), ignoredOpenedAuthoritiesSecurityConfigurerAdapter);
        }
    }

    @Override
    protected void initPreCheck() {
        Assert.notNull(jdbcDaoProcessorManager, "JdbcDaoProcessorManager 不能为null, 请将其注册为一个SpringBean");
        Assert.isTrue(!CollectionUtils.isEmpty(jdbcDaoProcessors)
                , "List<JdbcDaoProcessor> jdbcDaoProcessors 不能为空, 请将相关类注册为SpringBean, 具体可以参见 --> com.light.security.core.config.configuration.JdbcDaoProcessorBeanConfiguration");
        Assert.notNull(jdbcQuery, "JdbcQuery 不能为null, 请将其注册为一个SpringBean");
    }
}
