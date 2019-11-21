package com.light.security.core.access.model.tree.builder;

import com.light.security.core.access.model.Authority;
import com.light.security.core.access.model.MenuAuthority;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuAuthorityTreeBuilderTest {

    //测试Lambda表达式
    @Test
    public void testGroupBy(){
        List<MenuAuthority> menuAuthorities = new ArrayList<>();
        menuAuthorities.add((MenuAuthority) new MenuAuthority.Builder(1, "1", "/1")
                .parentId(1)
                .build());
        menuAuthorities.add((MenuAuthority) new MenuAuthority.Builder(2, "2", "/2")
                .parentId(2)
                .build());
        menuAuthorities.add((MenuAuthority) new MenuAuthority.Builder(3, "3", "/3")
                .parentId(3)
                .build());
        menuAuthorities.add((MenuAuthority) new MenuAuthority.Builder(4, "4", "/4")
                .parentId(null)
                .build());
        Map<Integer, List<Authority>> temp = menuAuthorities.stream().collect(Collectors.groupingBy(Authority::getAuthorityParentId));
        System.out.println(temp.getClass());
        temp.forEach((key, val) -> {
            System.out.println("key is " + key + "and the value is" + val);
        });
    }

}