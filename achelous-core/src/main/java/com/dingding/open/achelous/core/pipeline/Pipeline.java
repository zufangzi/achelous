/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.pipeline;

import java.util.List;

import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.support.Context;

/**
 * Pipeline用以组装plugin，并且指定plugin执行顺序以及执行方式
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public interface Pipeline {

    public enum PipelineState {
        END, OK, ERROR
    }

    /**
     * plugin打包与初始化
     * 
     * @param plugins plugin集合
     */
    <T extends Plugin> void bagging(List<T> plugins);

    /**
     * 拼装实时pipeline
     * 
     * @param context {@link Context}
     * @return {@link Pipeline}
     */
    Pipeline combine(Context context);

    /**
     * 执行函数
     */
    void call();

    void schedule(Context context);
}
