/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.plugin;

import java.util.Map;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.PipelineManager;
import com.dingding.open.achelous.core.cache.HierarchicalCache;
import com.dingding.open.achelous.core.pipeline.Pipeline.PipelineState;
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
        THREAD, PIPELINE, PIPELINE_PLUGIN
    }

    protected static final Object NEED_GO_AHEAD = new Object();

    private static final String CACHE_PIPELINE = "pipeline";

    private volatile String pluginName = null;
    private volatile ExecModes execMode = ExecModes.ALWAYS_IN;

    protected String attachConfig;

    @SuppressWarnings("unchecked")
    protected <M> M getCache(CacheLevel level, String key) {
        switch (level) {
            case THREAD:
                return (M) HierarchicalCache.getLevel3CacheByKey(key);
            case PIPELINE:
                return (M) HierarchicalCache.getLevel1Cache(
                        (String) HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE),
                        key);
            case PIPELINE_PLUGIN:
                return (M) HierarchicalCache.getLevel2Cache(
                        (String) HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE),
                        pluginName, key);
            default:
                throw new UnsupportedOperationException();
        }
    }

    protected <M> void setCache(CacheLevel level, String pluginName, String key, M value) {
        switch (level) {
            case THREAD:
                HierarchicalCache.setLevel3CacheKey(key, value);
                break;
            case PIPELINE:
                HierarchicalCache.setLevel1CacheKey((String) HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE), key,
                        value);
                break;
            case PIPELINE_PLUGIN:
                HierarchicalCache.setLevel2CacheKey((String) HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE),
                        pluginName, key, value);
                break;
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
                HierarchicalCache.setLevel1CacheKey((String) HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE), key,
                        value);
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
    public Plugin init() {
        pluginName = this.getClass().getAnnotation(PluginName.class).value();
        ExecMode mode = this.getClass().getAnnotation(ExecMode.class);
        if (mode != null) {
            execMode = mode.value();
        }
        return this;
    }

    /**
     * <pre>
     * 此处需要做多个处理.
     * 1.填充pipeline名.供后续mutex使用.
     * 2.增加计数.以提供如下场景使用:pub(brokerA).sub(brokerA).pub(brokerB).
     * </pre>
     * 
     * @see {@link PipelineManager#bagging}
     */
    @Override
    public PipelineState onNext(InvokerCore core, Context context) throws Throwable {
        // 如果一个请求会去调用多个pipeline,则调用下一个pipeline前先进行clear.如果pipeline还不存在,则新增pipeline, 存到Threadlocal里面.
        Object pipeline = HierarchicalCache.getLevel3CacheByKey(CACHE_PIPELINE);
        if (pipeline == null || !pipeline.toString().equals(context.getPipelineName())) {
            HierarchicalCache.setLevel3CacheKey(CACHE_PIPELINE, context.getPipelineName());
        }

        int count = 1;
        if (context.getPluginName2RepeatCounter().get(pluginName) == null) {
            context.getPluginName2RepeatCounter().put(pluginName, 2);
        } else {
            int oldValue = context.getPluginName2RepeatCounter().get(pluginName);
            count = ++oldValue;
            context.getPluginName2RepeatCounter().put(pluginName, count);
        }

        Map<String, String> ctMap = context.initPluginsConfig(pluginName + "_" + count);
        if (ctMap == null) {
            ctMap = context.initPluginsConfig(pluginName + "_" + 1);
        }

        Object rst = doWork(core, context, ctMap);
        if (rst == null) {
            return PipelineState.END;
        }
        context.getResult().set(rst);
        return PipelineState.OK;

    }

    @Override
    public void onCallBack(CallbackType type, InvokerCore core, Context context) {

    }

    @Override
    public void onError(InvokerCore core, Context context, Throwable t) {

    }

    @Override
    public void onCompleted(InvokerCore core, Context context) {

    }

    @Override
    public void attachConfigWhenPluginInitial(String attach) {
        attachConfig = attach;
    }

    public abstract Object doWork(InvokerCore core, Context context, Map<String, String> config) throws Throwable;

    @Override
    public ExecModes getExecMode() {
        return execMode;
    }
}
