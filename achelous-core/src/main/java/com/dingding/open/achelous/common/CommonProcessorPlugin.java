/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.common;

import java.util.Map;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.CommonPluginTypes;
import com.dingding.open.achelous.core.support.Context;

/**
 * 通用处理器插件
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
@PluginName(CommonPluginTypes.COM_PROC)
public class CommonProcessorPlugin extends AbstractPlugin {

    @Override
    public void onError(InvokerCore core, Context context, Throwable t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCompleted(InvokerCore core, Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object doWork(InvokerCore core, Context context, Map config) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }

}
