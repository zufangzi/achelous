/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.pipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.support.Context;

/**
 * 默认的pipeline实现。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class DftPipeline implements Pipeline {

    private List<Plugin> plugins;
    private List<Invoker> invokers;

    public void bagging(List<Plugin> plugins) {
        this.plugins = plugins;
        this.invokers = new ArrayList<Invoker>();
    }

    @Override
    public Pipeline combine(Context context) {
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
            public void invoke(Iterator<Invoker> iterator) {
                try {
                    now.onNext(iterator, context);
                    now.onCompleted(iterator, context);
                } catch (Throwable t) {
                    t.printStackTrace();
                    now.onError(iterator, context, t);
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
        };
        invokers.add(invoker);
        return next;
    }

    @Override
    public void call() {
        Iterator<Invoker> iterator = invokers.iterator();
        while (iterator.hasNext()) {
            Invoker invoker = iterator.next();
            invoker.invoke(iterator);
        }
    }
}
