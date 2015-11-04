/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.fluent;

import java.util.Arrays;

import com.dingding.open.achelous.common.ParrelPlugin;
import com.dingding.open.achelous.common.failover.FastFailPlugin;
import com.dingding.open.achelous.common.failover.RetryPlugin;
import com.dingding.open.achelous.core.pipeline.DftPipeline;
import com.dingding.open.achelous.core.pipeline.Pipeline;
import com.dingding.open.achelous.core.support.Context;

/**
 * 流式响应API
 * 
 * @author surlymo
 * @date Nov 4, 2015
 */
public class AchelousFlume {

    public static final AchelousFlume INSTANCE = new AchelousFlume();

    private AchelousFlume() {

    }

    private static final ThreadLocal<Pipeline> pipeline = new ThreadLocal<Pipeline>();

    public AchelousFlume fastfail() {
        getPipeline().bagging(Arrays.asList(new FastFailPlugin()));
        return this;
    }

    public AchelousFlume retryIfFail(String attach) {
        getPipeline().bagging(Arrays.asList(new RetryPlugin(attach)));
        return this;
    }

    public AchelousFlume pub() {
        getPipeline().bagging(Arrays.asList(new ParrelPlugin()));
        return this;
    }

    public void call() {
        pipeline.get().schedule(new Context());
    }

    private Pipeline getPipeline() {
        if (pipeline.get() == null) {
            pipeline.set(new DftPipeline());
        }
        return pipeline.get();
    }

}
