/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.support;

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

    /**
     * 快速失败
     */
    public static final String FAST_FAIL = "fail_fast";

    /**
     * 失败后重试
     */
    public static final String FAIL_RETRY = "fail_retry";

    public static final String ASYNC_LIST_SCHEDULER = "async";
}
