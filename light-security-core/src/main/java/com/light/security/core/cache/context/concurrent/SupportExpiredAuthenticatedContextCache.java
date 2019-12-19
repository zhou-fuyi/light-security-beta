package com.light.security.core.cache.context.concurrent;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.cache.model.InternalExpiredValueWrapper;
import com.light.security.core.util.signature.Signature;
import org.springframework.util.Assert;

import java.util.Iterator;

/**
 * @ClassName SupportExpiredSecurityContextCache
 * @Description 主体认证成功后的数据缓存实现(支持过期时间), 数据包装对象为<code>Authentication</code>类型
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public class SupportExpiredAuthenticatedContextCache extends AbstractSupportExpiredConcurrentContextCache<String, InternalExpiredValueWrapper<Authentication>> {

    /**
     * 用于解决内容重复的情况
     */
    private Signature signature;

    public SupportExpiredAuthenticatedContextCache(Signature signature){
        Assert.notNull(signature, "构造器不接受空值参数 --> signature is null");
        this.signature = signature;
    }

    @Override
    public void put(String now, InternalExpiredValueWrapper<Authentication> authenticationInternalExpiredValueWrapper) {
        Iterator<String> iterator = getContext().keySet().iterator();
        while (iterator.hasNext()){
            String origin = iterator.next();
            if (keyEquals(now, origin)){
                if (logger.isDebugEnabled()){
                    logger.debug("旧的key值为: {}, 即将删除对应的认证数据", origin);
                }
                elementExpiredEvent(origin, get(origin));
                remove(origin);
                break;
            }
        }
        super.put(now, authenticationInternalExpiredValueWrapper);
    }

    @Override
    protected void elementExpiredEvent(String key, InternalExpiredValueWrapper<Authentication> expiredValueWrapper) {
        // TODO: 2019-11-22 引入事件机制解决
        if (logger.isDebugEnabled()){
            logger.debug("你可以在删除 key is {} 的同时, 做别的关联操作", key);
        }
    }

    /**
     * 进行key的重复比对
     * @param origin_key
     * @param now_key
     * @return
     */
    private boolean keyEquals(String origin_key, String now_key){
        return signature.signatureEquals(origin_key, now_key);
    }
}
