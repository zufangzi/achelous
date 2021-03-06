/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.plugin;

import java.util.Iterator;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.pipeline.Pipeline.PipelineState;
import com.dingding.open.achelous.core.support.CallbackType;
import com.dingding.open.achelous.core.support.Context;

/**
 * 插件接口
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public interface Plugin {

    /**
     * 进行初始化。找到对应的pluginName
     */
    Plugin init();

    void onCallBack(CallbackType type, InvokerCore core, Context context);

    ExecModes getExecMode();

    /**
     * 核心执行函数。
     * 
     * @param invokers {@link Iterator} invoker的迭代器，游标以当前处理的invoker为起始。
     * @param context {@link Context} 上下文
     */
    PipelineState onNext(InvokerCore core, Context context) throws Throwable;

    /**
     * 处理错误时的执行函数。
     * 
     * @param invokers {@link Iterator} invoker的迭代器，游标以当前处理的invoker为起始。
     * @param context {@link Context} 上下文
     * @param t {@link Throwable} 异常类
     */
    void onError(InvokerCore core, Context context, Throwable t);

    /**
     * 正常处理结束之后的调用函数
     * 
     * @param invokers {@link Iterator} invoker的迭代器，游标以当前处理的invoker为起始。
     * @param context {@link Context} 上下文
     */
    void onCompleted(InvokerCore core, Context context);

    void attachConfigWhenPluginInitial(String attach);
}
