/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.dingding.open.achelous.core.parser.CoreConfig;
import com.dingding.open.achelous.core.parser.Parser;
import com.dingding.open.achelous.core.parser.properties.PropertiesParser;
import com.dingding.open.achelous.core.pipeline.DftPipeline;
import com.dingding.open.achelous.core.pipeline.Pipeline;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.Context;
import com.dingding.open.achelous.core.support.PluginMeta;
import com.dingding.open.achelous.core.support.Suite;

/**
 * pipeline管理器。进行所有pipeline的解析和初始化
 * 
 * @see {@link Pipeline}
 * @author surlymo
 * @date Oct 27, 2015
 */
@Component
public class PipelineManager {

    private static final Map<String, Pipeline> pipelinePool = new HashMap<String, Pipeline>();

    private static Parser parser;

    private static final String DEFAULT_PLUGIN_PATH = "com.dingding.open.achelous.common";

    private static final Map<String, Plugin> pluginMap = new HashMap<String, Plugin>();

    private static String defaultPipeline = null;

    private static final Map<String, Map<String, Map<String, String>>> suite2Plugin2FeatureMap =
            new HashMap<String, Map<String, Map<String, String>>>();

    private static final List<String> pluginPaths = new ArrayList<String>();

    private static CoreConfig coreConfig = null;

    private static volatile AtomicBoolean defaultPipelineSwitch = new AtomicBoolean(false);

    private static volatile AtomicBoolean coreInitSwitch = new AtomicBoolean(false);

    /**
     * 以下都是plugin+pipeline级别可以前置的一些变量存放.
     */
    public static final Map<String, ReentrantLock> locks = new ConcurrentHashMap<String, ReentrantLock>();
    public static final Map<String, Boolean> exhaustFlags = new ConcurrentHashMap<String, Boolean>();
    public static final Map<String, AtomicBoolean> mutexs = new ConcurrentHashMap<String, AtomicBoolean>();

    private static Pipeline getPipeline(String name) {
        return pipelinePool.get(name);
    }

    /**
     * 进行核心的初始化工作
     */
    public synchronized void coreInit(ApplicationContext context) {

        // sychronized确保其他初始化线程阻塞。此处再来判断是否初始化过。
        if (!coreInitSwitch.compareAndSet(false, true)) {
            return;
        }

        if (parser == null) {
            parser = new PropertiesParser();
        }

        // 解析获取各类配置
        coreConfig = parser.parser();

        // 进行全部参数的处理
        globalConfigProcess(coreConfig.getGlobalConfig());

        // 对所有通用的plugin进行初始化
        initByPath(DEFAULT_PLUGIN_PATH);

        // 对spring-based的plugin bean进行加载。
        if (context != null) {
            Map<String, Plugin> plugins = context.getBeansOfType(Plugin.class);
            if (plugins != null && !plugins.isEmpty()) {
                for (Plugin plugin : plugins.values()) {
                    pluginMap.put(plugin.getClass().getAnnotation(PluginName.class).value(), plugin);
                }
            }
        }

        bagging();
    }

    private static void bagging() {
        for (Suite suite : coreConfig.getSuites()) {
            Map<String, Integer> pluginRepeatTimeMap = new HashMap<String, Integer>();
            Map<String, Map<String, String>> pluginsFeatureMap = new HashMap<String, Map<String, String>>();
            Pipeline pipeline = new DftPipeline();
            // 首先将自己pipeline下的所有plugin进行实例化，并灌入pool中去。
            pipelinePool.put(suite.getName(), pipeline);
            List<Plugin> plugins = new ArrayList<Plugin>();

            for (PluginMeta meta : suite.getPluginMetas()) {

                if (pluginMap.get(meta.getPluginName()) == null) {
                    continue;
                }

                int sequence = 1;
                if (pluginRepeatTimeMap.get(meta.getPluginName()) == null) {
                    pluginRepeatTimeMap.put(meta.getPluginName(), 1);
                } else {
                    int oldValue = pluginRepeatTimeMap.get(meta.getPluginName());
                    sequence = ++oldValue;
                    pluginRepeatTimeMap.put(meta.getPluginName(), sequence);
                }

                // TODO 多pipeline下会覆盖
                Plugin plugin = pluginMap.get(meta.getPluginName()).init(suite.getName());
                plugins.add(plugin);
                pluginsFeatureMap.put(meta.getPluginName() + sequence, meta.getFeature2ValueMap());
                locks.put(suite.getName() + meta.getPluginName(), new ReentrantLock());
                exhaustFlags.put(suite.getName() + meta.getPluginName(), false);
                mutexs.put(suite.getName() + meta.getPluginName(), new AtomicBoolean(false));
            }
            pipeline.bagging(plugins);

            if (defaultPipelineSwitch.compareAndSet(false, true)) {
                defaultPipeline = suite.getName();
            }

            suite2Plugin2FeatureMap.put(suite.getName(), pluginsFeatureMap);
        }
    }

    public synchronized static void checkPluginPath(String path) {
        if (pluginPaths.contains(path)) {// 如果存在，则返回
            return;
        }
        // 不存在则开始补充初始化
        pluginPaths.add(path);
        initByPath(path);
        bagging();
    }

    /**
     * 目前是基于目录递归new出来的。随后需要考虑plugin是Spring Bean的情况
     * 
     * TODO
     * 
     * @param path
     */
    private static void initByPath(String path) {
        String initPath = PipelineManager.class.getClassLoader().getResource(path.replace(".", "/")).getPath();
        initPath = initPath.replace("/", File.separator);

        File file = new File(initPath);

        if (!file.exists()) {
            return;
        }

        String prefix = path;
        for (String str : file.list()) {
            File tmpFile = new File(initPath + File.separator + str);
            if (tmpFile.isDirectory()) {
                initByPath(path + "." + str);
                continue;
            }
            if (str.contains("$")) {
                continue;
            }

            Plugin plugin = Factory.getEntity(prefix + "." + str.split("\\.")[0]);
            // 此处该判断暂时不打开。
            // if (plugin.getClass().getAnnotation(Component.class) == null) {
            PluginName name = plugin.getClass().getAnnotation(PluginName.class);
            if (pluginMap.get(name.value()) == null) {
                pluginMap.put(name.value(), plugin);
            }
            // }
        }
    }

    /**
     * 进行全部参数的初始化
     * 
     * @param globalConfig 全局参数
     */
    @SuppressWarnings("unchecked")
    private static void globalConfigProcess(Map<String, Object> globalConfig) {

        if (globalConfig.get(CoreConfig.GLOBAL_PLUGIN_PATH) == null) {
            return;
        }

        // initPlugins
        for (String path : (List<String>) globalConfig.get(CoreConfig.GLOBAL_PLUGIN_PATH)) {
            if (path != null) {
                pluginPaths.add(path);
            }
            initByPath(path);
        }
    }

    public void call(Context context) {
        call(defaultPipeline, context);
    }

    public void call(String pipeline, Context context) {
        // coreInit(null);
        Pipeline pipe = getPipeline(pipeline);
        context.setPipelineName(pipeline);
        context.setPlugin2ConfigMap(suite2Plugin2FeatureMap.get(pipeline));
        pipe.combine(context).call();
    }

    public void addDefaultConfig(String pluginName, String feature, String value) {
        for (String pipelineKey : suite2Plugin2FeatureMap.keySet()) {
            Map<String, Map<String, String>> pluginValus = suite2Plugin2FeatureMap.get(pipelineKey);
            for (String pluginKey : pluginValus.keySet()) {
                if (pluginKey.contains(pluginName) && !pluginValus.get(pluginKey).containsKey(feature)) {
                    pluginValus.get(pluginKey).put(feature, value);
                }
            }
        }
    }

}
