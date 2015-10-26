package com.dingding.open.achelous.core.pipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.support.Context;

public class DftPipeline implements Pipeline {

    @SuppressWarnings("static-access")
    public void bagging(List<Plugin> plugins) {
        this.plugins.set(plugins);
        this.invokers.set(new ArrayList<Invoker>());
    }

    @Override
    public Pipeline combine(Context context) {
        Plugin combinedPlugin = plugins.get().get(0);
        for (int i = 1; i <= plugins.get().size() - 1; i++) {
            combinedPlugin = combine(combinedPlugin, plugins.get().get(i), context);
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
        };
        invokers.get().add(invoker);
        return next;
    }

    @Override
    public void call() {
        Iterator<Invoker> iterator = invokers.get().iterator();
        while (iterator.hasNext()) {
            Invoker invoker = iterator.next();
            invoker.invoke(iterator);
        }
    }
}
