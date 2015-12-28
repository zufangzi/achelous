/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.dingding.open.achelous.core.parser.CoreConfig;
import com.dingding.open.achelous.core.parser.Parser;
import com.dingding.open.achelous.core.parser.properties.PropertiesParser;
import com.dingding.open.achelous.core.pipeline.DftPipeline;
import com.dingding.open.achelous.core.pipeline.Pipeline;
import com.dingding.open.achelous.core.plugin.ExecModes;
import com.dingding.open.achelous.core.plugin.NextPlugins;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.plugin.PrePlugins;
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

    public static final Map<String, Map<String, Map<String, String>>> suite2Plugin2FeatureMap =
            new HashMap<String, Map<String, Map<String, String>>>();

    private static final List<String> pluginPaths = new ArrayList<String>();

    private static CoreConfig coreConfig = null;

    private static volatile AtomicBoolean defaultPipelineSwitch = new AtomicBoolean(false);

    private static volatile AtomicBoolean coreInitSwitch = new AtomicBoolean(false);

    public static final Map<String, AtomicBoolean> exhaustMarks = new HashMap<String, AtomicBoolean>();
    public static final Map<String, Integer> pipelineExhaustCnts = new HashMap<String, Integer>();

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

            Map<String, Map<String, String>> pluginsFeatureMap = new HashMap<String, Map<String, String>>();
            Pipeline pipeline = new DftPipeline();
            // 首先将自己pipeline下的所有plugin进行实例化，并灌入pool中去。
            pipelinePool.put(suite.getName(), pipeline);
            List<Plugin> plugins = new ArrayList<Plugin>();

            List<PluginMeta> metas = suite.getPluginMetas();
            for (int i = 0; i < metas.size(); i++) {

                if (pluginMap.get(metas.get(i).getPluginName()) == null) {
                    continue;
                }

                Plugin plugin = pluginMap.get(metas.get(i).getPluginName()).init();

                if (plugin.getClass().getAnnotation(PrePlugins.class) != null) {
                    // 默认就往下一个plugin TODO
                    Class pluginClazz = plugin.getClass().getAnnotation(PrePlugins.class).value()[0];
                    String prePluginName = ((PluginName) pluginClazz.getAnnotation(PluginName.class)).value();
                    if ((i == 0 || !metas.get(i - 1).getPluginName().equals(prePluginName))) {
                        Plugin prePlugin = pluginMap.get(prePluginName).init();
                        plugins.add(prePlugin);
                        System.out.println("i is: " + i + " " + metas.get(i).getPluginName());
                        System.out.println("add pre plugin..." + prePluginName);
                        addPluginConfig(suite, prePluginName, prePlugin, new HashMap<String, String>(),
                                pluginsFeatureMap);
                    }
                }

                plugins.add(plugin);
                addPluginConfig(suite, metas.get(i).getPluginName(), plugin, metas.get(i).getFeature2ValueMap(),
                        pluginsFeatureMap);

                if (plugin.getClass().getAnnotation(NextPlugins.class) != null) {
                    // 默认就往下一个plugin TODO
                    Class pluginClazz = plugin.getClass().getAnnotation(NextPlugins.class).value()[0];
                    String nextPluginName = ((PluginName) pluginClazz.getAnnotation(PluginName.class)).value();
                    // 如果是最后一个,或者下一个plugin和希望的下一个plugin不相同,则插入一个.
                    if ((i == (metas.size() - 1)) || !metas.get(i + 1).getPluginName().equals(nextPluginName)) {
                        Plugin nextPlugin = pluginMap.get(nextPluginName).init();
                        plugins.add(nextPlugin);
                        System.out.println("i is: " + i + " " + metas.get(i).getPluginName());
                        System.out.println("add next plugin..." + nextPluginName);
                        addPluginConfig(suite, nextPluginName, nextPlugin, new HashMap<String, String>(),
                                pluginsFeatureMap);
                    }
                }

            }
            pipeline.bagging(plugins);

            if (defaultPipelineSwitch.compareAndSet(false, true)) {
                defaultPipeline = suite.getName();
            }

            suite2Plugin2FeatureMap.put(suite.getName(), pluginsFeatureMap);
        }
    }

    private static void addPluginConfig(Suite suite, String pluginName, Plugin plugin,
            Map<String, String> metaPluginConfigs, Map<String, Map<String, String>> pluginsFeatureMap) {
        Map<String, Integer> pluginRepeatTimeMap = new HashMap<String, Integer>();

        int sequence = 1;
        if (pluginRepeatTimeMap.get(pluginName) == null) {
            pluginRepeatTimeMap.put(pluginName, 1);
        } else {
            int oldValue = pluginRepeatTimeMap.get(pluginName);
            sequence = ++oldValue;
            pluginRepeatTimeMap.put(pluginName, sequence);
        }

        // 记录plugin如果是只能执行一次的.则后面的请求需要把plugin踢出pipeline. 考虑到一个pipeline里面可能会多次调用.
        // 以pipeline + plugin + index作为唯一key.
        if (plugin.getExecMode().equals(ExecModes.ONLY_ONCE)) {
            exhaustMarks.put(suite.getName() + pluginName + sequence,
                    new AtomicBoolean(false));

            // 记录
            if (pipelineExhaustCnts.get(suite.getName()) == null) {
                pipelineExhaustCnts.put(suite.getName(), 1);
            } else {
                int cnt = pipelineExhaustCnts.get(suite.getName());
                pipelineExhaustCnts.put(suite.getName(), ++cnt);
            }
        }

        pluginsFeatureMap.put(pluginName + sequence, metaPluginConfigs);
    }

    public static void refreshPipelinePlugins(String pipeline) {
        synchronized (suite2Plugin2FeatureMap) {
            Map<String, Map<String, String>> plugins = suite2Plugin2FeatureMap.get(pipeline);

            Iterator<Entry<String, Map<String, String>>> iterator = plugins.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Map<String, String>> entry = iterator.next();
                String key = entry.getKey();
                for (String pluginKey : pluginMap.keySet()) {
                    if (key.contains(pluginKey) && pluginMap.get(pluginKey).getExecMode().equals(ExecModes.ONLY_ONCE)) {
                        System.out.println("remove: " + key);
                        iterator.remove();
                        // pipelinepool不需要concurrent
                        pipelinePool.get(pipeline).deletePlugin(key);
                    }
                }
            }

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
