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
//    private Signature signature;
//
//    public SupportExpiredAuthenticatedContextCache(Signature signature){
//        Assert.notNull(signature, "构造器不接受空值参数 --> signature is null");
//        this.signature = signature;
//    }

    /**
     * 数据插入
     * @param now
     * @param authenticationInternalExpiredValueWrapper
     */
    @Override
    public void put(String now, InternalExpiredValueWrapper<Authentication> authenticationInternalExpiredValueWrapper) {
        /**
         * 这里进行主动控制是因为key值每一次都不一样(即使加密的私钥是一样的), 所以需要提前主动控制重复
         *
         *
         * 由于将签名改用为{@link com.light.security.core.util.signature.MD5Signature}进行实现, 可以
         * 控制相同私钥生成的签名一致, 所以可以利用散列表对于重复key值的默认处理方式, 不用再进行主动控制,
         * 所以将下面主动控制代码注释
         */
        /*Iterator<String> iterator = getContext().keySet().iterator();
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
        }*/
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
//        return signature.signatureEquals(origin_key, now_key);
        return false;
    }
}
