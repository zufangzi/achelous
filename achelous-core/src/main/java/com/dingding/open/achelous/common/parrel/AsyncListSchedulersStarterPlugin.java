/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.common.parrel;

import com.dingding.open.achelous.core.Factory;
import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.NextPlugins;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.CommonPluginTypes;
import com.dingding.open.achelous.core.support.ConcurrentCooker;
import com.dingding.open.achelous.core.support.ConfigConstant;
import com.dingding.open.achelous.core.support.Context;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步调度插件
 * 
 * @author surlymo
 * @date Nov 10, 2015
 */
@Component
@PluginName(CommonPluginTypes.ASYNC_LIST_START_SCHEDULER)
@NextPlugins(AsyncListSchedulersPlugin.class)
public class AsyncListSchedulersStarterPlugin extends AbstractPlugin {

    private static final Logger logger = Logger.getLogger(AsyncListSchedulersStarterPlugin.class);

    public static volatile ConfigConstant CONF_COOKER = new ConfigConstant("cooker", "");

    @SuppressWarnings({ "rawtypes" })
    @Override
    public Object doWork(InvokerCore core, final Context context, Map<String, String> config) throws Throwable {
        logger.info("[ACHELOUS]now in async starter processor...");
        // executor初始化 TODO,需要的话就开大点
        ExecutorService executor = Executors.newFixedThreadPool(4);

        ConcurrentCooker cooker = Factory.getEntity(config.get(CONF_COOKER.getName()).toString());
        // 如果无cooker，那么就判断result是否是list，是的话，则直接拆开；否则直接扔进去处理。此处留 TODO
        if (cooker != null) {
            setCache(CacheLevel.PIPELINE_PLUGIN,
                    AsyncListSchedulersPlugin.class.getAnnotation(PluginName.class).value(), "cooker", cooker);
            setCache(CacheLevel.PIPELINE_PLUGIN,
                    AsyncListSchedulersPlugin.class.getAnnotation(PluginName.class).value(), "executor", executor);
        } else {
            // TODO
        }
        System.out.println("END starter...");
        return context.getResult().get();
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
