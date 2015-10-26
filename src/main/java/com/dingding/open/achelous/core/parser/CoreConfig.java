/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.parser;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.dingding.open.achelous.core.support.Suite;

/**
 * 配置的核心数据。可以从XML或者properties中获取
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
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
