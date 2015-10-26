package com.dingding.open.achelous.core.support;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Suite {

    private String name;
    private List<PluginMeta> pluginMetas;


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
