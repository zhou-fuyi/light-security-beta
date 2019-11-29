package com.light.security.core.config.configuration;

import com.light.security.core.access.model.tree.builder.AuthorityTreeBuilder;
import com.light.security.core.access.model.tree.builder.ElementAuthorityTreeBuilder;
import com.light.security.core.access.model.tree.builder.MenuAuthorityTreeBuilder;
import com.light.security.core.access.model.tree.builder.manager.AuthorityTreeBuilderManger;
import com.light.security.core.access.model.tree.builder.manager.TreeBuilderManager;
import com.light.security.core.authentication.context.InternalSecurityContext;
import com.light.security.core.authentication.context.holder.SecurityContextHolder;
import com.light.security.core.properties.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName GlobalSecurityConfiguration
 * @Description 全局配置器, 先于别的配置器执行
 * @Author ZhouJian
 * @Date 2019-11-21
 */
@Configuration
public class GlobalSecurityConfiguration {

    /**
     * 全局配置器
     * @return
     */
    @Bean
    public SecurityProperties securityProperties(){
        return new SecurityProperties();
    }

    /**
     * 元素树构建器
     * @return
     */
    @Bean
    public AuthorityTreeBuilder elementAuthorityTreeBuilder(){
        return new ElementAuthorityTreeBuilder();
    }

    /**
     * 菜单树构建器
     * @return
     */
    @Bean
    public AuthorityTreeBuilder menuAuthorityTreeBuilder(){
        return new MenuAuthorityTreeBuilder();
    }

    /**
     * TODO 后续需要进行优化, 必须进行优化
     * 树构建器管理器
     * @param authorityTreeBuilders
     * @return
     */
    @Bean
    public TreeBuilderManager treeBuilderManager(List<AuthorityTreeBuilder> authorityTreeBuilders){
        AuthorityTreeBuilderManger authorityTreeBuilderManger = new AuthorityTreeBuilderManger();
        authorityTreeBuilderManger.setAuthorityTreeBuilders(authorityTreeBuilders);
        authorityTreeBuilders.forEach(item -> {
            item.setTreeBuilderManager(authorityTreeBuilderManger);
        });
        return authorityTreeBuilderManger;
    }

    /**
     * SecurityContextHolder注册
     * @return
     */
    public SecurityContextHolder securityContextHolder(){
        return new SecurityContextHolder(new InternalSecurityContext());
    }

}
