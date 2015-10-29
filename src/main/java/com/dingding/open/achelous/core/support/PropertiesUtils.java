/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.support;

import java.util.Map;
import java.util.Properties;

/**
 * properties工具类。结合ConfigConstant来设置默认值
 * 
 * @see {@link ConfigConstant}
 * @author surlymo
 * @date Oct 29, 2015
 */
public class PropertiesUtils {

    /**
     * 放置参数到properties类中
     * 
     * @param properties {@link Properties}
     * @param key 键
     * @param value {@link ConfigConstant} 带有默认值
     * @param config 参数map。
     */
    public static void put(Properties properties, String key, ConfigConstant value, Map<String, String> config) {
        if (value.getName() == null && value.getDefaultConfig() != null) {
            properties.put(key, value.getDefaultConfig());
        } else if (value.getName() == null || config.get(value.getName()) == null) {
            return;
        } else {
            properties.put(key, config.get(value.getName()));
        }
    }
}
