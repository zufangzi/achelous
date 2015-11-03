/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.common;

import com.dingding.open.achelous.core.plugin.PluginTypes;

/**
 * 插件类型。如果需要扩展，也可以通过String来自定义。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class CommonPluginTypes extends PluginTypes {
    /**
     * IO阻塞方式并发插件。
     */
    public static final String PARREL_IO = "parrel_io";

    /**
     * 通用的处理器插件。
     */
    public static final String COM_PROC = "com_proc";
}
