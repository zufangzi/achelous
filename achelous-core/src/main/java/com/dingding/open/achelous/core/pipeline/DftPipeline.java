/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.pipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.PipelineManager;
import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.plugin.ExecModes;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.plugin.PluginName;
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

    private static final ThreadLocal<Boolean> isCommonPipeline = new ThreadLocal<Boolean>();
    private static final ThreadLocal<Boolean> keepOnlyOnce = new ThreadLocal<Boolean>();
    private static final ThreadLocal<ArrayList<Invoker>> invokers = new ThreadLocal<ArrayList<Invoker>>();
    private static final ThreadLocal<String> pipelineName = new ThreadLocal<String>();

    private Object flag = new Object();

    @Override
    public <T extends Plugin> void bagging(List<T> plugins) {
        this.plugins.addAll(plugins);
    }

    @Override
    public <T extends Plugin> void deletePlugin(String pluginNameWithSequnece) {
        synchronized (plugins) {
            Iterator<Plugin> iterator = plugins.iterator();
            while (iterator.hasNext()) {
                if (pluginNameWithSequnece
                        .contains(iterator.next().getClass().getAnnotation(PluginName.class).value())) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public Pipeline combine(Context context) {
        pipelineName.set(context.getPipelineName());
        invokers.set(new ArrayList<Invoker>());
        Plugin combinedPlugin = plugins.get(0);
        for (int i = 1; i <= plugins.size() - 1; i++) {
            combinedPlugin = combine(combinedPlugin, plugins.get(i), context);
        }
        combine(combinedPlugin, null, context);
        return this;
    }

    private Plugin combine(final Plugin now, final Plugin next, final Context context) {

        // 只会在刚开始的几次中出现.后续的话应该要被从plugins中剔除
        if (now.getExecMode().equals(ExecModes.ONLY_ONCE)) {
            String pluginName = now.getClass().getAnnotation(PluginName.class).value();

            Integer sequence = context.getPluginName2RepeatCounter().get(pluginName);

            // 如果没抢到后需要跳过之前,要sequnce+1才行,否则不需要+1.
            if (sequence == null) {
                sequence = 1;
            }

            AtomicBoolean exhaustMark = PipelineManager.exhaustMarks
                    .get(context.getPipelineName()
                            + now.getClass().getAnnotation(PluginName.class).value() + sequence);
            if (!exhaustMark.compareAndSet(false, true)) {
                isCommonPipeline.set(false);
                if (sequence == 1) {
                    context.getPluginName2RepeatCounter().put(pluginName, 2);
                } else {
                    context.getPluginName2RepeatCounter().put(pluginName, ++sequence);
                }
                return next; // 成功的就接着往下处理,如果没有成功, 则说明没抢成功锁, 此时则跳过这个plugin
            }
            // 成功抢到的, 不需要设置context.在AbstractPlugin中自然会设置一次
            // 需要设置信号量.
            isCommonPipeline.set(true);
            keepOnlyOnce.set(true);
        }

        Invoker invoker = new Invoker() {

            @Override
            public boolean invoke(InvokerCore core) {
                try {
                    PipelineState state = now.onNext(core, context);
                    now.onCompleted(core, context);
                    count2Notify(this, core.getNotCommonTimes());
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
            public boolean isOnlyOnceInvoker() {
                return now.getExecMode().equals(ExecModes.ONLY_ONCE);
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
        // 如果是带有单次执行plugin的pipeline. 则
        if (isCommonPipeline.get() != null && !isCommonPipeline.get().booleanValue()) {
            try {
                System.out.println("now begin to wait flag semph...");
                synchronized (flag) {
                    flag.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Integer times = 0;
        InvokerCore core = new InvokerCore();
        core.setNotCommonTimes(times);
        core.setInvokers(invokerList);
        for (int i = 0; i < invokerList.size(); i = core.getCurrentIndex().get()) {
            Invoker invoker = invokerList.get(i);
            core.getCurrentIndex().set(i);
            boolean goOn = invoker.invoke(core);

            if (!goOn) {
                // System.out.println("now need to break...");
                break;
            }
        }
        invokers.remove();
        isCommonPipeline.remove();
        keepOnlyOnce.remove();
    }

    private void count2Notify(Invoker invoker, Integer times) {
        if (keepOnlyOnce.get() != null && invoker.isOnlyOnceInvoker()) {
            if (++times == PipelineManager.pipelineExhaustCnts.get(pipelineName.get())) {
                System.out.println("now notify all...");
                synchronized (flag) {
                    PipelineManager.refreshPipelinePlugins(pipelineName.get());
                    flag.notifyAll();
                }
            }
        }
    }

    @Override
    public void schedule(Context context) {
        combine(context).call();
    }

}
