/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.invoker;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.support.CallbackType;

/**
 * 
 * 让plugin被延迟调用。这样即可以在整个pipeline组装完成之后，再进行调度。 从而使得一些特殊case可以被解决，如并发plugin，map-reduce plugin等。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public interface Invoker {

    boolean isOnlyOnceInvoker();

    /**
     * 真实调用方法
     * 
     * @param iterator pipeline执行到现在为止的迭代器
     */
    boolean invoke(InvokerCore core);

    void callback(CallbackType type, InvokerCore core);

    /**
     * 设置是否跳过该执行器
     * 
     * @param needSkip 是否跳过
     */
    void setSkip(boolean needSkip);

    /**
     * 获取是否跳过该invoker的执行
     * 
     * @return 是否跳过
     */
    boolean isNeedSkip();
}
