/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core;

import org.apache.log4j.Logger;

/**
 * pipeline调用入口
 * 
 * @author surlymo
 * @date Nov 3, 2015
 */
public abstract class AbstractEntrance {

    protected Logger logger = Logger.getLogger(this.getClass());

    protected AbstractEntrance() {
        PluginPath pluginPath = this.getClass().getAnnotation(PluginPath.class);
        PipelineManager.checkPluginPath(pluginPath.value());
    }
}
