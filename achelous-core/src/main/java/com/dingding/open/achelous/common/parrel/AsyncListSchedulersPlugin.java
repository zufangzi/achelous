/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.common.parrel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.plugin.PrePlugins;
import com.dingding.open.achelous.core.support.CommonPluginTypes;
import com.dingding.open.achelous.core.support.ConcurrentCooker;
import com.dingding.open.achelous.core.support.ConfigConstant;
import com.dingding.open.achelous.core.support.Context;

/**
 * 异步调度插件
 * 
 * @author surlymo
 * @date Nov 10, 2015
 */
@PluginName(CommonPluginTypes.ASYNC_LIST_SCHEDULER)
@PrePlugins(AsyncListSchedulersStarterPlugin.class)
public class AsyncListSchedulersPlugin extends AbstractPlugin {

    private static final Logger logger = Logger.getLogger(AsyncListSchedulersPlugin.class);

    public static volatile ConfigConstant CONF_STREAMS = new ConfigConstant("streams", "1");

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object doWork(InvokerCore core, final Context context, Map<String, String> config) throws Throwable {
        logger.info("[ACHELOUS]now in async processor...");

        ConcurrentCooker cooker = (ConcurrentCooker) getCache(CacheLevel.PIPELINE_PLUGIN, "cooker");
        // 先拿到上一个的响应打成list

        int threads = 0;
        if (config.get(CONF_STREAMS.getName()) != null) {
            threads = Integer.valueOf(config.get(CONF_STREAMS.getName()));
        } else {
            threads = Integer.valueOf(CONF_STREAMS.getDefaultConfig());
        }
        System.out.println("outer config: " + config);
        final List mtls = (List) (cooker.call(context.getResult().get(), threads, config));

        // 随后将链条丢入执行器执行
        Executor executor = getCache(CacheLevel.PIPELINE_PLUGIN, "executor");
        for (final Object mtl : mtls) {
            executor.execute(new AsyncJob(core, mtl, context));
        }
        return null;
    }

    class AsyncJob implements Runnable {

        private int currentIndex;
        private InvokerCore core;
        private Object mtl;
        private Context context;

        public AsyncJob(InvokerCore core, Object mtl, final Context context) {
            this.core = core;
            this.currentIndex = core.getCurrentIndex().get();
            this.mtl = mtl;
            this.context = context;
        }

        @Override
        public void run() {
            context.getResult().set(mtl);
            core.getCurrentIndex().set(currentIndex + 1);
            int size = core.getInvokers().size();
            logger.info("[ACHELOUS]Thread: " + Thread.currentThread().getName() + " current index is: " + currentIndex);
            for (int i = currentIndex + 1; i < size; i++) {
                // 确保不会出现多线程问题。
                core.getInvokers().get(i).invoke(core);
                // 回滚游标。确保从头再来一遍
                core.getCurrentIndex().set(core.getCurrentIndex().get() - 1);
            }
        }
    }
}
