/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.plugin;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.dingding.open.achelous.core.cache.HierarchicalCache;
import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.support.CallbackType;
import com.dingding.open.achelous.core.support.Context;

/**
 * 插件抽象类
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public abstract class AbstractPlugin implements Plugin {

    protected enum CacheLevel {
        THREAD,
        PIPELINE,
        PIPELINE_PLUGIN
    }

    private static final String CACHE_PIPELINE = "pipeline";

    private String pluginName = null;

    protected String attachConfig;

    public static final Map<String, AtomicBoolean> MUTEX = new ConcurrentHashMap<String, AtomicBoolean>();
    public static final String MUTEX_MARK = "mutex";

    protected boolean exhaust() {
        return MUTEX.get(HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE).toString() + pluginName)
                .compareAndSet(
                        false, true);
    }

    @SuppressWarnings("unchecked")
    protected <M> M getCache(CacheLevel level, String key) {
        switch (level) {
            case THREAD:
                return (M) HierarchicalCache.getLevel3CacheByKey(key);
            case PIPELINE:
                return (M) HierarchicalCache.getLevel1Cache(
                        (String) HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE), key);
            case PIPELINE_PLUGIN:
                return (M) HierarchicalCache.getLevel2Cache(
                        (String) HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE), pluginName, key);
            default:
                throw new UnsupportedOperationException();
        }
    }

    protected <M> void setCache(CacheLevel level, String key, M value) {
        switch (level) {
            case THREAD:
                HierarchicalCache.setLevel3CacheKey(key, value);
                break;
            case PIPELINE:
                HierarchicalCache.setLevel1CacheKey((String) HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE),
                        key, value);
                break;
            case PIPELINE_PLUGIN:
                HierarchicalCache.setLevel2CacheKey((String) HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE),
                        pluginName, key, value);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public Plugin init(String pipeline) {
        pluginName = this.getClass().getAnnotation(PluginName.class).value();
        MUTEX.put(pipeline + pluginName, new AtomicBoolean(false));
        return this;
    }

    @Override
    public void onNext(Iterator<Invoker> invokers, Context context) throws Throwable {
        if (HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE) == null) {
            HierarchicalCache.setLevel3CacheKey(CACHE_PIPELINE, context.getPipelineName());
        }
        int count = 1;
        if (context.getPluginName2RepeatCounter().get(pluginName) == null) {
            context.getPluginName2RepeatCounter().put(pluginName, 1);
        } else {
            int oldValue = context.getPluginName2RepeatCounter().get(pluginName);
            count = ++oldValue;
            context.getPluginName2RepeatCounter().put(pluginName, count);
        }

        doWork(invokers, context, context.initPluginsConfig(pluginName + count));
    }

    @Override
    public void onCallBack(CallbackType type, Iterator<Invoker> invokers, Context context) {

    }

    @Override
    public void onError(Iterator<Invoker> invokers, Context context, Throwable t) {

    }

    @Override
    public void onCompleted(Iterator<Invoker> invokers, Context context) {

    }

    @Override
    public void attachConfigWhenPluginInitial(String attach) {
        attachConfig = attach;
    }

    public abstract void doWork(Iterator<Invoker> invokers, Context context, Map<String, String> config)
            throws Throwable;
}
