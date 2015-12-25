/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.common.failover;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.CallbackType;
import com.dingding.open.achelous.core.support.CommonPluginTypes;
import com.dingding.open.achelous.core.support.Context;

/**
 * 失败重试插件
 * 
 * @author surlymo
 * @date Nov 4, 2015
 */
@Component
@PluginName(CommonPluginTypes.FAIL_RETRY)
public class RetryPlugin extends AbstractPlugin {

    public RetryPlugin() {

    }

    /**
     * 可以match上fluent java api new plugin的场景。但是properties配置的场景下，可能会出现pipeline里面多同一个plugin的case。
     * 
     * @param attach
     */
    public RetryPlugin(String attach) {
        super.attachConfigWhenPluginInitial(attach);
    }

    private static final Logger logger = Logger.getLogger(RetryPlugin.class);

    @Override
    public Object doWork(InvokerCore core, Context context, Map<String, String> config) throws Throwable {
        String attach = attachConfig;
        int totalCnt = 0;
        long sleep = 0L;
        if (context.getAttach() != null) {
            attach = context.getAttach();
        }
        if (attach != null) {
            totalCnt = Integer.valueOf(attach.split(";")[0]);
            sleep = Long.valueOf(attach.split(";")[1]);
        } else {
            totalCnt = Integer.valueOf(config.get("times"));
            sleep = Long.valueOf(config.get("sleep"));
        }

        Invoker invoker = core.nextN(1);
        int retryTimes = totalCnt;
        boolean isOk = false;
        while (retryTimes-- >= 0) {
            try {
                invoker.invoke(core);
                isOk = true;
                break;
            } catch (Throwable t) {
                logger.warn("[ACHELOUS]fail retry strategy become effective...");
                if (retryTimes <= 0) {
                    logger.error("[ACHELOUS]fail to recover from error. Total retry times: " + totalCnt);
                    break;
                }
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        if (!isOk) {
            invoker.callback(CallbackType.ERROR, core);
        }
        return null;
    }

}
