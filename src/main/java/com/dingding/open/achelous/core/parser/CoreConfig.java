package com.dingding.open.achelous.core.parser;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.dingding.open.achelous.core.support.Suite;

public class CoreConfig {
    private List<Suite> suites;
    private Map<String, String> globalConfig;

    public List<Suite> getSuites() {
        return suites;
    }

    public void setSuites(List<Suite> suites) {
        this.suites = suites;
    }

    public Map<String, String> getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(Map<String, String> globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
