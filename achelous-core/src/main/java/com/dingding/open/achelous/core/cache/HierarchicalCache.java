/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分为三级cache。分别为pipeline、pipeline+plugin、以及threadlocal级别的.如果pipeline里面用到多次同一个plugin目前没有做很好适配。 如有类似需求可以加入index来做cache
 * 
 * TODO
 * 
 * @author surlymo
 * @date Oct 28, 2015
 */
@SuppressWarnings("unchecked")
public class HierarchicalCache {
    private static final ThreadLocal<Map<String, Object>> level3Cache = new ThreadLocal<Map<String, Object>>();
    private static final Map<String, Map<String, Object>> level2Cache =
            new ConcurrentHashMap<String, Map<String, Object>>();
    private static final Map<String, Map<String, Object>> level1Cache =
            new ConcurrentHashMap<String, Map<String, Object>>();

    /**
     * 获取三级cache。为threadlocal级别
     * 
     * @param key cache的key
     * @return cache的value
     */
    public static <T> T getLevel3CacheByKey(String key) {
        if (level3Cache.get() == null) {
            level3Cache.set(new HashMap<String, Object>());
        }
        return (T) level3Cache.get().get(key);
    }

    /**
     * 设置三级cache的key-value
     * 
     * @param key cache的key
     * @param value cache的value
     */
    public static <T> void setLevel3CacheKey(String key, T value) {
        if (level3Cache.get() == null) {
            level3Cache.set(new HashMap<String, Object>());
        }
        level3Cache.get().put(key, value);
    }

    /**
     * 获取二级cache，无需同步锁。pipeline+plugin级别
     * 
     * @param pipeline 套件名
     * @param plugin 插件名
     * @param key cache的key
     * @return cache的value
     */
    public static <T> T getLevel2Cache(String pipeline, String plugin, String key) {
        return (T) level2Cache.get(pipeline + plugin).get(key);
    }

    /**
     * 牺牲了性能来cache的。能避免用则避免用。一般只有在符合一定条件情况下才会调用到。所以对整体性能影响可以忽略
     * 
     * @param pipeline 套件名
     * @param plugin 插件名
     * @param key 插入的key
     * @param value 插入的value
     */
    public static synchronized <T> void setLevel2CacheKey(String pipeline, String plugin, String key, T value) {
        Map<String, Object> cache = null;
        if ((cache = level2Cache.get(pipeline + plugin)) == null) {
            cache = new HashMap<String, Object>();
            level2Cache.put(pipeline + plugin, cache);
        }
        cache.put(key, value);
    }

    /**
     * 获取一级cache。
     * 
     * @param pipeline 套件名
     * @param key cache的key
     * @return cache的value
     */
    public static <T> T getLevel1Cache(String pipeline, String key) {
        return (T) level1Cache.get(pipeline).get(key);
    }

    /**
     * 牺牲性能来cache。一级cache。pipeline级别的。但用到的场景也不多。所以对性能的影响基本忽略
     * 
     * @param pipeline 套件名
     * @param key cache的key
     * @param value cache的value
     */
    public static synchronized <T> void setLevel1CacheKey(String pipeline, String key, T value) {
        Map<String, Object> cache = null;
        if ((cache = level1Cache.get(pipeline)) == null) {
            cache = new HashMap<String, Object>();
            level1Cache.put(pipeline, cache);
        }
        cache.put(key, value);
    }
}
