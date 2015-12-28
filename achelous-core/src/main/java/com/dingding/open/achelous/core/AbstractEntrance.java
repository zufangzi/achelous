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
        // init(false);
    }

    protected AbstractEntrance(boolean flag) {
        init(flag);
    }

    private void init(boolean flag) {
        if (flag || this.getClass().getAnnotation(Component.class) == null) {
            manager = new PipelineManager();
            coreInit(null);
        }
    }

    @SuppressWarnings("unused")
    @Autowired
    private Factory factory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        manager = Factory.getEntity("pipelineManager");
        coreInit(applicationContext);
    }

    private void coreInit(ApplicationContext context) {
        FilePath filePath = this.getClass().getAnnotation(FilePath.class);
        if (filePath != null) {
            System.setProperty("file_name", filePath.value());
        }
        manager.coreInit(context);
        PluginPath pluginPath = this.getClass().getAnnotation(PluginPath.class);
        if (pluginPath != null && context == null) {
            PipelineManager.checkPluginPath(pluginPath.value());
        }
        getDefaultProps();
    }

    private void getDefaultProps() {
        DefaultProps props = this.getClass().getAnnotation(DefaultProps.class);
        if (props == null) {
            return;
        }
        String[] args = props.value();
        for (String arg : args) {// 格式为 "aysnc.cooker=xxx.xx.xx.xxCooker "
            String key = arg.split("=")[0];
            String value = arg.split("=")[1];
            String pluginName = key.split("\\.")[0];
            String pluginFeature = key.split("\\.")[1];
            manager.addDefaultConfig(pluginName, pluginFeature, value);
        }
    }
}
