/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * @author surlymo
 * @date 12:13:34 AM Dec 26, 2015
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface FilePath {

    /**
     * 
     * @return
     */
    String value();
}
