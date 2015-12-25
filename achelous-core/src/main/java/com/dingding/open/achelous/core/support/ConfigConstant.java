/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.support;

/**
 * 配置项
 * 
 * @author surlymo
 * @date Nov 3, 2015
 */
public class ConfigConstant {
    private String name;
    private String defaultConfig;

    public ConfigConstant() {
        this.name = this.toString();
    }

    public ConfigConstant(String name) {
        this.name = name;
    }

    public ConfigConstant(String name, String defaultConfig) {
        this.name = name;
        this.defaultConfig = defaultConfig;
    }

    public String getName() {
        return name;
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }
}
