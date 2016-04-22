package com.dingding.open.achelous.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dingding.open.achelous.common.failover.RetryPlugin;

/**
 * 
 * @author surlymo
 * @date Nov 8, 2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:achelous-core.xml")
public class PipelineManagerTest {

    @Autowired
    private PipelineManager manager;

    @Autowired
    private RetryPlugin plugin;

    @Test
    public void test_api_by_spring_startup_normal() {
        Assert.assertTrue(manager != null);
    }

}
