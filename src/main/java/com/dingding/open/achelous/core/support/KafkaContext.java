/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.support;

/**
 * plugin执行过程中的上下文封装。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class KafkaContext<T, M> extends Context {
    private T key;
    private M value;

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public M getValue() {
        return value;
    }

    public void setValue(M value) {
        this.value = value;
    }

}
