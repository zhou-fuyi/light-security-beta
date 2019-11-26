package com.light.security.core.authentication.listener;

import com.light.security.core.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.context.ApplicationListener;

/**
 * @ClassName InteractiveAuthenticationSuccessListener
 * @Description 交互性身份验证成功监听器
 * @Author ZhouJian
 * @Date 2019-11-25
 */
public class InteractiveAuthenticationSuccessListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {

        // TODO: 2019-11-25 待完成
    }
}
