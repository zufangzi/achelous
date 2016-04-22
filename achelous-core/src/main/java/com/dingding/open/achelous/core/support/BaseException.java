/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.support;

import com.dingding.open.achelous.core.pipeline.Pipeline.PipelineState;

/**
 * 基础异常。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -578012046622316765L;

    private PipelineState state;

    public BaseException() {
        super();
    }

    public BaseException(PipelineState state) {
        this.state = state;
    }

    public BaseException(String reason, Throwable error) {
        super(reason, error);
    }

    public PipelineState getState() {
        return state;
    }

    public void setState(PipelineState state) {
        this.state = state;
    }

}
