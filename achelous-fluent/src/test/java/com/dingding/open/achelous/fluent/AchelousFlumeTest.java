/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.fluent;

import org.junit.Assert;
import org.junit.Test;

/**
 * 单测
 * 
 * @author surlymo
 * @date Nov 4, 2015
 */
public class AchelousFlumeTest {

    @Test(expected = Throwable.class)
    public void test_fastfail_normal() {
        AchelousFlume.INSTANCE.fastfail().pub().call();
        Assert.fail();
    }

    @Test(expected = Throwable.class)
    public void test_failretry_normal() {
        try {
            AchelousFlume.INSTANCE.retryIfFail("3;1000").pub().call();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        Assert.fail();
    }
}
