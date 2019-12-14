package com.light.security.core.cache.context.linked;

import com.light.security.core.cache.context.AbstractContextCache;
import com.light.security.core.cache.context.LinkedContextCache;
import com.light.security.core.cache.context.concurrent.AbstractConcurrentContextCache;

import java.util.LinkedHashMap;

/**
 * @ClassName AbstractLinkedContextCache
 * @Description 使用<code>LinkedHashMap</code>作为容器实现缓存的通用实现
 * @Author ZhouJian
 * @Date 2019-12-14
 */
public abstract class AbstractLinkedContextCache<K, V> extends AbstractContextCache implements LinkedContextCache<K, V> {

    //容器的真正实例
    private LinkedHashMap<K, V> linkedHashCache = new LinkedHashMap<>();

    protected AbstractLinkedContextCache(){
        getCacheInstanceMap().forEach((k, v) -> {
            if (v instanceof AbstractConcurrentContextCache){
                logger.info("实例全类名: {}, 实例成员 -> {} <- 打印信息：{} ", k, linkedHashCache.getClass().getName(), ((AbstractLinkedContextCache) v).linkedHashCache);
            }
        });
    }

    @Override
    public void put(K k, V v) {
        linkedHashCache.put(k, v);
    }

    @Override
    public V get(K k) {
        return linkedHashCache.get(k);
    }

    @Override
    public void remove(K k) {
        linkedHashCache.remove(k);
    }

    @Override
    public void cleanContext() {
        linkedHashCache.clear();
    }

    @Override
    public int getContextCount() {
        return linkedHashCache.size();
    }

    @Override
    public LinkedHashMap<K, V> getContext() {
        return linkedHashCache;
    }
}
