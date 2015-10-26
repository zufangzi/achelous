/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.support;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -578012046622316765L;

    public BaseException() {
        super();
    }

    public BaseException(String reason, Throwable error) {
        super(reason, error);
    }

}
