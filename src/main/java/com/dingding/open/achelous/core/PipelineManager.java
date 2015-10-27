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
public class PipelineManager {

    private static final Map<String, Pipeline> pipelinePool = new HashMap<String, Pipeline>();

    private static Parser parser;

    private static final String PLUGINPATH = "com/dingding/open/achelous/core/plugin/impl";

    private static final Map<String, Plugin> pluginMap = new HashMap<String, Plugin>();

    static {
        coreInit();
    }

    public static Pipeline getPipeline(String name) {
        return pipelinePool.get(name);
    }

    /**
     * 进行核心的初始化工作
     */
    private static synchronized void coreInit() {

        if (parser == null) {
            parser = new PropertiesParser();
        }

        // 将plugin进行实例化
        initPlugins();

        // 解析获取各类配置
        CoreConfig config = parser.parser();

        // 进行全部参数的处理
        globalConfigProcess(config.getGlobalConfig());

        // 进行bundle的pipeline组装
        for (Suite suite : config.getSuites()) {
            Pipeline pipeline = new DftPipeline();
            // 首先将自己pipeline下的所有plugin进行实例化，并灌入pool中去。
            pipelinePool.put(suite.getName(), pipeline);
            List<Plugin> plugins = new ArrayList<Plugin>();

            for (PluginMeta meta : suite.getPluginMetas()) {
                plugins.add(pluginMap.get(meta.getPluginName()));
            }
            pipeline.bagging(plugins);
        }
    }

    private static void initPlugins() {
        File file = new File(PipelineManager.class.getClassLoader().getResource("").getPath() + PLUGINPATH);
        String prefix = PLUGINPATH.replace("/", ".");
        for (String str : file.list()) {

            Plugin plugin = null;
            try {
                plugin = (Plugin) Class.forName(prefix + "." + str.split("\\.")[0]).newInstance();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            PluginName name = plugin.getClass().getAnnotation(PluginName.class);
            pluginMap.put(name.value().name(), plugin);
        }
    }

    /**
     * 进行全部参数的初始化
     * 
     * @param globalConfig 全局参数
     */
    public static void globalConfigProcess(Map<String, String> globalConfig) {
        // TODO
    }

    public static void main(String[] args) throws Exception {

        Pipeline pipe = getPipeline("msgCenter");
        pipe.combine(new Context()).call();

    }
}
