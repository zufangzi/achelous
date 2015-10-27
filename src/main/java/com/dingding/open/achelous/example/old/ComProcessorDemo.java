package com.dingding.open.achelous.example.old;

import java.util.Iterator;

import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.support.Context;

public class ComProcessorDemo implements Plugin {

    @Override
    public void onNext(Iterator<Invoker> invokers, Context context) {
        System.out.println("hi, i am now in com processor...");
    }

    @Override
    public void onError(Iterator<Invoker> invokers, Context context, Throwable t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCompleted(Iterator<Invoker> invokers, Context context) {
        // TODO Auto-generated method stub

    }

}
