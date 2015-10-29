/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.parser.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.dingding.open.achelous.core.parser.CoreConfig;
import com.dingding.open.achelous.core.parser.Parser;
import com.dingding.open.achelous.core.support.BaseException;
import com.dingding.open.achelous.core.support.OrderProperties;
import com.dingding.open.achelous.core.support.PluginMeta;
import com.dingding.open.achelous.core.support.Suite;

/**
 * properties解析器
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class PropertiesParser implements Parser {

    private static final Logger logger = Logger.getLogger(PropertiesParser.class);
    private static final String FILENAME = "seda.properties";
    private static final Map<String, String> keyValues = new LinkedHashMap<String, String>();

    private static final String GLOBAL_CONFIG_PREFIX = "global";
    private static final String SUITE_STAGE_FLAG = "stage";

    @Override
    public synchronized CoreConfig parser() {

        // 先进性初始化，将key-value全部塞入keyValues
        init();

        // 将所有的配置按照suite进行划分。
        return createSuites();

    }

    private static void init() {
        Properties prop = new OrderProperties();
        InputStream input = ClassLoader.getSystemResourceAsStream(FILENAME);
        try {
            prop.load(input);
        } catch (IOException e) {
            logger.error("[Achelous]error occur while parser core properties file", e);
            throw new BaseException();
        }

        for (Object initKey : prop.keySet()) {
            String key = (String) initKey;
            String value = prop.getProperty(key);
            keyValues.put(key, value);
        }
    }

    private static CoreConfig createSuites() {
        CoreConfig coreConfig = new CoreConfig();
        Map<String, Suite> suiteName2ObjMap = new HashMap<String, Suite>();

        // suiteName+pluginName作为key，plugin的具体元数据作为value。
        Map<String, PluginMeta> pluginName2ObjMap = new HashMap<String, PluginMeta>();

        for (Entry<String, String> entry : keyValues.entrySet()) {
            String[] keyMetas = entry.getKey().split("\\.");

            // 如果是全局参数，则处理。模式为"global.config"
            if (keyMetas.length == 3 && !keyMetas[1].equals("plugin")) {// 如果是三截的且为"suite.plugin.config"的模式

                String suiteName = keyMetas[0];
                if (suiteName2ObjMap.get(suiteName) == null) {
                    Suite suite = new Suite();
                    suite.setName(suiteName);
                    suiteName2ObjMap.put(suiteName, suite);
                }

                String pluginName = keyMetas[1];
                String configKey = keyMetas[2];
                String configValue = entry.getValue();

                if (pluginName2ObjMap.get(suiteName + pluginName) == null) {
                    PluginMeta meta = new PluginMeta();
                    meta.setPluginName(pluginName);
                    meta.getFeature2ValueMap().put(configKey, configValue);
                    pluginName2ObjMap.put(suiteName + pluginName, meta);
                    suiteName2ObjMap.get(suiteName).getPluginMetas().add(meta);
                } else {
                    pluginName2ObjMap.get(suiteName + pluginName).getFeature2ValueMap().put(configKey, configValue);
                }

            } else if (keyMetas.length == 3 && keyMetas[0].equals("global") && keyMetas[1].equals("plugin")
                    && keyMetas[2].equals("path")) { // 如果是约定的插件的路径，则将信息进行装填
                coreConfig.getGlobalConfig().put(CoreConfig.GLOBAL_PLUGIN_PATH,
                        Arrays.asList(entry.getValue().split(";")));
            } else if (keyMetas.length == 2 && keyMetas[1].equals(SUITE_STAGE_FLAG)) {// 如果是"suite.stage"模式的
                // TODO 填充suite2PluginIndexMap.类型Map<String, Map<String, Integer>>。基于这个再进行一次排序。
            }
        }

        // 再从每个suite里面的所有key-value切割出plugin以及对应key-value的关系。

        Suite[] suites = new Suite[suiteName2ObjMap.values().size()];
        suiteName2ObjMap.values().toArray(suites);
        coreConfig.setSuites(Arrays.asList(suites));
        return coreConfig;
    }
}
