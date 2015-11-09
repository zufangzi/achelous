/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * pipeline调用入口
 * 
 * @author surlymo
 * @date Nov 3, 2015
 */
public abstract class AbstractEntrance implements ApplicationContextAware {

    protected Logger logger = Logger.getLogger(this.getClass());

    protected static volatile PipelineManager manager;

    protected AbstractEntrance() {
        init(false);
    }

    protected AbstractEntrance(boolean flag) {
        init(flag);
    }

    private void init(boolean flag) {
        if (flag || this.getClass().getAnnotation(Component.class) == null) {
            manager = new PipelineManager();
            manager.coreInit(null);
            PluginPath pluginPath = this.getClass().getAnnotation(PluginPath.class);
            PipelineManager.checkPluginPath(pluginPath.value());
        }
    }

    @Autowired
    private Factory factory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        manager = Factory.getEntity("pipelineManager");
        manager.coreInit(applicationContext);
        PluginPath pluginPath = this.getClass().getAnnotation(PluginPath.class);
        PipelineManager.checkPluginPath(pluginPath.value());

    }
}
