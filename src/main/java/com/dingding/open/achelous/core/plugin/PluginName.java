/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.plugin;

/**
 * 插件名。用以进行插件处理类的路由。支持classpath以及plugin.impl两个目录。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public @interface PluginName {
    /**
     * 插件名
     * 
     * @return {@link PluginTypes}
     */
    PluginTypes value();
}
