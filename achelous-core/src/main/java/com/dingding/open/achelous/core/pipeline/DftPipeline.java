/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.support.CallbackType;
import com.dingding.open.achelous.core.support.Context;

/**
 * 默认的pipeline实现。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class DftPipeline implements Pipeline {

    private List<Plugin> plugins = new ArrayList<Plugin>();

    private static final ThreadLocal<ArrayList<Invoker>> invokers = new ThreadLocal<ArrayList<Invoker>>();

    @Override
    public <T extends Plugin> void bagging(List<T> plugins) {
        this.plugins.addAll(plugins);
    }

    @Override
    public Pipeline combine(Context context) {
        invokers.set(new ArrayList<Invoker>());
        Plugin combinedPlugin = plugins.get(0);
        for (int i = 1; i <= plugins.size() - 1; i++) {
            combinedPlugin = combine(combinedPlugin, plugins.get(i), context);
        }
        combine(combinedPlugin, null, context);
        return this;
    }

    private Plugin combine(final Plugin now, final Plugin next, final Context context) {
        Invoker invoker = new Invoker() {

            @Override
            public boolean invoke(InvokerCore core) {
                try {
                    PipelineState state = now.onNext(core, context);
                    now.onCompleted(core, context);
                    if (state.equals(PipelineState.END)) {
                        // 如果等于结束，则中断
                        return false;
                    }
                    return true;
                } catch (Throwable t) {
                    now.onError(core, context, t);
                    throw new RuntimeException(t);
                } finally {
                    core.getCurrentIndex().set((core.getCurrentIndex().get() + 1));
                }
            }

            @Override
            public void setSkip(boolean needSkip) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean isNeedSkip() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void callback(CallbackType type, InvokerCore core) {
                now.onCallBack(type, core, context);
            }
        };
        invokers.get().add(invoker);
        return next;
    }

    @Override
    public void call() {
        ArrayList<Invoker> invokerList = invokers.get();
        for (int i = 0; i < invokerList.size(); i++) {

            Invoker invoker = invokerList.get(i);
            InvokerCore core = new InvokerCore();
            core.getCurrentIndex().set(i);
            core.setInvokers(invokerList);

            if (!invoker.invoke(core)) {
                System.out.println("now need to break...");
                break;
            }
        }
        invokers.remove();
    }

    @Override
    public void schedule(Context context) {
        combine(context).call();
    }

}
