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
 * @date 11:34:26 AM Dec 25, 2015
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DefaultProps {

    /**
     * argsA:valA, argsB:valB
     * 
     * @return
     */
    String[] value();
}
