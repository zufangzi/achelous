/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 每个plugin的元数据
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class PluginMeta {
    private String pluginName;
    private Map<String, String> feature2ValueMap = new HashMap<String, String>();

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public Map<String, String> getFeature2ValueMap() {
        return feature2ValueMap;
    }

    public void setFeature2ValueMap(Map<String, String> feature2ValueMap) {
        this.feature2ValueMap = feature2ValueMap;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
