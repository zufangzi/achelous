/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.support;

import java.util.HashMap;
import java.util.Map;

/**
 * plugin执行过程中的上下文封装。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class Context {

    private Map<String, Integer> pluginName2RepeatCounter = new HashMap<String, Integer>();
    private Map<String, Map<String, String>> plugin2ConfigMap = new HashMap<String, Map<String, String>>();
    private String pipelineName;
    private String attach;
    private Map<String, Object> contextMap = new HashMap<String, Object>();

    public Map<String, Integer> getPluginName2RepeatCounter() {
        return pluginName2RepeatCounter;
    }

    public void setPluginName2RepeatCounter(Map<String, Integer> pluginName2RepeatCounter) {
        this.pluginName2RepeatCounter = pluginName2RepeatCounter;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public Map<String, Object> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, Object> contextMap) {
        this.contextMap = contextMap;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public Map<String, String> initPluginsConfig(String pluginName) {
        return plugin2ConfigMap.get(pluginName);
    }

    public Map<String, Map<String, String>> getPlugin2ConfigMap() {
        return plugin2ConfigMap;
    }

    public void setPlugin2ConfigMap(Map<String, Map<String, String>> plugin2ConfigMap) {
        this.plugin2ConfigMap = plugin2ConfigMap;
    }

}
