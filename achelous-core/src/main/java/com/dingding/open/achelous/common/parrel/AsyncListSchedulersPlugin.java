package com.dingding.open.achelous.common.parrel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import rx.functions.Func2;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.CommonPluginTypes;
import com.dingding.open.achelous.core.support.ConfigConstant;
import com.dingding.open.achelous.core.support.Context;

@PluginName(CommonPluginTypes.ASYNC_LIST_SCHEDULER)
public class AsyncListSchedulersPlugin extends AbstractPlugin {

    private static final Logger logger = Logger.getLogger(AsyncListSchedulersPlugin.class);

    private ExecutorService executor;
    private static final ConfigConstant CONF_STREAMS = new ConfigConstant("streams", "1");

    @Override
    public Object doWork(InvokerCore core, final Context context, Map<String, String> config)
            throws Throwable {
        logger.info("[ACHELOUS]now in async processor...");
        if (exhaust()) {
            // executor初始化
            executor = Executors.newFixedThreadPool(4);
            int threads = 0;
            if (config.get(CONF_STREAMS.getName()) != null) {
                threads = Integer.valueOf(config.get(CONF_STREAMS.getName()));
            } else {
                threads = Integer.valueOf(CONF_STREAMS.getDefaultConfig());
            }
            // 通pipeline多plugin的不支持
            setCache(CacheLevel.PIPELINE_PLUGIN, "threads", threads);
        }

        // 如果无cooker，那么就判断result是否是list，是的话，则直接拆开；否则直接扔进去处理。此处留 TODO
        if (context.getResultCooker() == null) {

        }
        // 如果有cooker，则按照cooker处理。
        final Func2<Object, Integer, List> func = (Func2<Object, Integer, List>) context.getResultCooker();

        // 先拿到上一个的响应打成list
        final List mtls = func.call(context.getResult().get(), (int) getCache(CacheLevel.PIPELINE_PLUGIN, "threads"));

        // 随后将链条丢入执行器执行
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
                // 确保不会出现多线程问题
                core.getInvokers().get(i).invoke(core);
            }
        }
    }
}
