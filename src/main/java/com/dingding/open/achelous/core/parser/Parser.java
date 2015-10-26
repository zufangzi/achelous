/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.parser;

/**
 * 解析器。可以是XML或者properties解析器
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public interface Parser {

    /**
     * 执行解析过程，将结果存入 {@link CoreConfig}对象结果中。
     * 
     * @return {@link CoreConfig}
     */
    CoreConfig parser();
}
