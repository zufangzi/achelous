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

    private Map<String, Map<String, String>> plugin2ConfigMap = new HashMap<String, Map<String, String>>();
    private String pipelineName;

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
