/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.common.failover;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.CommonPluginTypes;
import com.dingding.open.achelous.core.support.Context;

/**
 * 快速失败策略插件
 * 
 * @author surlymo
 * @date Nov 4, 2015
 */
@Component
@PluginName(CommonPluginTypes.FAST_FAIL)
public class FastFailPlugin extends AbstractPlugin {

    private static final Logger logger = Logger.getLogger(FastFailPlugin.class);

    @Override
    public Object doWork(InvokerCore core, Context context, Map<String, String> config) throws Throwable {
        // 取出下一个执行器
        try {
            int nextIndex = core.getCurrentIndex().get() + 1;
            core.getInvokers().get(nextIndex).invoke(core);
        } catch (Throwable t) {
            logger.error("[ACHELOUS]fast fail strategy become effective");
            throw t;
        }
        return null;
    }

}
