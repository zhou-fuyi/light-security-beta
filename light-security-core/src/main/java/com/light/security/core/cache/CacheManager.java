package com.light.security.core.cache;

/**
 * @InterfaceName CacheManager
 * @Description 缓存管理器
 * @Author ZhouJian
 * @Date 2019-11-22
 */
public interface CacheManager {

    // TODO: 2019-11-22 暂时想不到解决方案，可以管理内部和外部缓存，根据配置动态的替换缓存使用策略，还是暂时先完成内部缓存实现的方案，后续再进行优化

    /**
     * ThreadLocal实现的应该算不上缓存，应该将其放弃
     * 使用Map的结构便可以与外部缓存进行整合了
     * 对，可以考虑这么做
     */
}
