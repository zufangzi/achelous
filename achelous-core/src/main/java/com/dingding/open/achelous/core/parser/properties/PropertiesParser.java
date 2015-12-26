/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.parser.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.dingding.open.achelous.core.parser.CoreConfig;
import com.dingding.open.achelous.core.parser.Parser;
import com.dingding.open.achelous.core.parser.properties.OrderProperties.SortedUnuniqueKey;
import com.dingding.open.achelous.core.support.BaseException;
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
    private static String FILENAME = "achelous.properties";
    private static final Set<SortedUnuniqueKey> keyValus = new LinkedHashSet<SortedUnuniqueKey>();

    private static final String GLOBAL_CONFIG_PREFIX = "global";
    private static final String SUITE_STAGE_FLAG = "stage";

    public static final String BIG_COVER_SPLITER = "\t";
    public static final String SMALL_COVER_SPLITER = "=";

    @Override
    public synchronized CoreConfig parser() {

        // 先进行初始化，将key-value全部塞入keyValues
        init();

        // 将所有的配置按照suite进行划分。
        return createSuites();

    }

    private static void init() {
        // TODO 多maven工程下的配置管理？
        Properties prop = new OrderProperties();
        if (StringUtils.isNotBlank(System.getProperty("file_name"))) {
            FILENAME = System.getProperty("file_name");
        }

        // 如果需要覆盖,那么就用System变量来搞
        String value = System.getProperty("cover_data");
        if (value != null) {
            for (String line : value.split(BIG_COVER_SPLITER)) {
                String[] keyAndValue = line.split(SMALL_COVER_SPLITER);
                prop.put(keyAndValue[0], keyAndValue[1]);
            }
        } else {
            // 不可用ClassLoader.getSystemResourceAsStream.
            InputStream input = null;
            try {
                input = new FileInputStream(new File(
                        Thread.currentThread().getContextClassLoader().getResource(FILENAME).getFile()));
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                prop.load(input);
            } catch (IOException e) {
                logger.error("[Achelous]error occur while parser core properties file", e);
                throw new BaseException();
            }
        }

        for (Object obj : prop.keySet()) {
            keyValus.add((SortedUnuniqueKey) obj);
        }

    }

    private static CoreConfig createSuites() {
        CoreConfig coreConfig = new CoreConfig();
        Map<String, Suite> suiteName2ObjMap = new HashMap<String, Suite>();

        String formerPlugin = null;
        String formerSuite = null;
        PluginMeta formerPluginMeta = null;
        for (SortedUnuniqueKey entry : keyValus) {
            String[] keyMetas = entry.getKey().toString().split("\\.");

            // 如果是全局参数，则处理。模式为"global.config"
            if (keyMetas.length == 3 && keyMetas[0].equals(GLOBAL_CONFIG_PREFIX) && keyMetas[1].equals("plugin")
                    && keyMetas[2].equals("path")) { // 如果是约定的插件的路径，则将信息进行装填
                coreConfig.getGlobalConfig().put(CoreConfig.GLOBAL_PLUGIN_PATH,
                        Arrays.asList(entry.getValue().toString().split(";")));
            } else if (keyMetas.length == 2 && keyMetas[1].equals(SUITE_STAGE_FLAG)) {// 如果是"suite.stage"模式的
                // TODO 填充suite2PluginIndexMap.类型Map<String, Map<String,
                // Integer>>。基于这个再进行一次排序。
            } else {

                String suiteName = null;
                String pluginName = null;
                String configKey = null;
                String configValue = entry.getValue().toString();
                if (keyMetas.length == 3) {// 完整版
                    suiteName = keyMetas[0];
                    pluginName = keyMetas[1];
                    configKey = keyMetas[2];
                } else { // 简化版，把pipename隐藏
                    suiteName = "default-pipe";
                    pluginName = keyMetas[0];
                    configKey = keyMetas[1];
                }

                Suite suite = null;
                // 如果不等，说明已经进入下一suite了。此时新增个suite对象
                if (!suiteName.equals(formerSuite)) {
                    suite = new Suite();
                    suite.setName(suiteName);
                    suiteName2ObjMap.put(suiteName, suite);

                    formerSuite = suiteName;

                } else {
                    suite = suiteName2ObjMap.get(suiteName);
                }

                // 如果不等，说明已经进入下一plugin了。此时新增个plugin对象
                if (!pluginName.equals(formerPlugin)) {
                    PluginMeta meta = new PluginMeta();
                    meta.setPluginName(pluginName);
                    meta.getFeature2ValueMap().put(configKey, configValue);
                    suite.getPluginMetas().add(meta);
                    formerPluginMeta = meta;

                    // 最后，将formerPlugin进行更新。
                    formerPlugin = pluginName;

                } else {// 如果相等，就是在plugin里面加feature就行了
                    formerPluginMeta.getFeature2ValueMap().put(configKey, configValue);
                }

            }
        }

        // 再从每个suite里面的所有key-value切割出plugin以及对应key-value的关系。

        Suite[] suites = new Suite[suiteName2ObjMap.values().size()];
        suiteName2ObjMap.values().toArray(suites);
        coreConfig.setSuites(Arrays.asList(suites));
        return coreConfig;
    }
}
