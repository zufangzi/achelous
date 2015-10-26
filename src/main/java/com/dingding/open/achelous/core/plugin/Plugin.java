package com.dingding.open.achelous.core.plugin;

import java.util.Iterator;

import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.support.Context;

public interface Plugin {

    void onNext(Iterator<Invoker> invokers, Context context);

    void onError(Iterator<Invoker> invokers, Context context, Throwable t);

    void onCompleted(Iterator<Invoker> invokers, Context context);
}
