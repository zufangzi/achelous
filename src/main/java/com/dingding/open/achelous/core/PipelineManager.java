/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dingding.open.achelous.core.parser.CoreConfig;
import com.dingding.open.achelous.core.parser.Parser;
import com.dingding.open.achelous.core.pipeline.DftPipeline;
import com.dingding.open.achelous.core.pipeline.Pipeline;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.plugin.impl.CommonProcessorPlugin;
import com.dingding.open.achelous.core.plugin.impl.KafkaConsumerPlugin;
import com.dingding.open.achelous.core.plugin.impl.KafkaProducerPlugin;
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
public abstract class PipelineManager {

    private static Map<String, Pipeline> pipelinePool;

    protected static Parser parser;

    static {
        coreInit();
    }

    /**
     * 进行核心的初始化工作
     */
    public static synchronized void coreInit() {
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
                try {
                    plugins.add((Plugin) Class.forName(meta.getPluginName()).newInstance());

                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();// TODO
                }
            }
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

    public static void main(String[] args) {

        Plugin a = new KafkaConsumerPlugin();
        Plugin b = new CommonProcessorPlugin();
        Plugin c = new KafkaProducerPlugin();

        Pipeline pipe = new DftPipeline();
        pipe.bagging(Arrays.asList(a, b, c));
        pipe.combine(new Context()).call();
    }
}
