package com.light.security.core.cache.context;

import java.util.Map;

/**
 * @InterfaceName ConcurrentContextCache
 * @Description 使用<code>ConcurrentHashMap</code>作为容器实现缓存的顶层接口
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public interface ConcurrentContextCache<K, V> extends ContextCache {

    /**
     * 放入一则数据
     * @param k
     * @param v
     */
    void put(K k, V v);

    /**
     * 根据k获取指定数据
     * @param k
     * @return 如果k不存在, 则返回null
     */
    V get(K k);

    /**
     * 根据k值删除数据, 如果k不存在则不进行操作
     * @param k
     */
    void remove(K k);

    /**
     * 获取当前容器实例
     * @return
     */
    @Override
    Map<K, V> getContext();
}
