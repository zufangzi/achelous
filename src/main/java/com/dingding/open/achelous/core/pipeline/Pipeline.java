package com.dingding.open.achelous.core.pipeline;

import java.util.List;

import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.support.Context;

public interface Pipeline {

    ThreadLocal<List<Plugin>> plugins = new ThreadLocal<List<Plugin>>();
    ThreadLocal<List<Invoker>> invokers = new ThreadLocal<List<Invoker>>();

    void bagging(List<Plugin> plugins);

    Pipeline combine(Context context);

    void call();
}
