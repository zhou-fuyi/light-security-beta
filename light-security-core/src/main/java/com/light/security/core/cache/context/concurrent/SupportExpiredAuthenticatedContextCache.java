package com.light.security.core.cache.context.concurrent;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.cache.model.InternalExpiredValueWrapper;

import java.util.Iterator;

/**
 * @ClassName SupportExpiredSecurityContextCache
 * @Description 主体认证成功后的数据缓存实现(支持过期时间), 数据包装对象为<code>Authentication</code>类型
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class SupportExpiredAuthenticatedContextCache extends AbstractSupportExpiredConcurrentContextCache<String, InternalExpiredValueWrapper<Authentication>> {

    @Override
    public void put(String s, InternalExpiredValueWrapper<Authentication> authenticationInternalExpiredValueWrapper) {
        Iterator<String> iterator = getContext().keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            if (keyEquals(s, key)){
                logger.info("重复认证产生数据删除");
                elementExpiredEvent(key, get(key));
                remove(key);
                break;
            }
        }
        super.put(s, authenticationInternalExpiredValueWrapper);
    }

    @Override
    protected void elementExpiredEvent(String s, InternalExpiredValueWrapper<Authentication> expiredValueWrapper) {
        // TODO: 2019-11-22 引入事件机制解决 
    }

    /**
     * 进行key的重复比对
     * @param origin_key
     * @param now_key
     * @return
     */
    private boolean keyEquals(String origin_key, String now_key){
        // TODO: 2019-11-22 暂时不进行实现，因为目前还没有想好使用什么作为key
        return false;
    }
}
