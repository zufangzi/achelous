/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 一个plugin组合即为一个套件，将会最终被转成一个pipeline
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class Suite {

    private String name;
    private List<PluginMeta> pluginMetas = new ArrayList<PluginMeta>();
    private Map<String, String> commonConfig = new HashMap<String, String>();

    public Map<String, String> getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(Map<String, String> commonConfig) {
        this.commonConfig = commonConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PluginMeta> getPluginMetas() {
        return pluginMetas;
    }

    public void setPluginMetas(List<PluginMeta> pluginMetas) {
        this.pluginMetas = pluginMetas;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
