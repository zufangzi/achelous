package com.dingding.open.achelous.core.parser.properties;

import org.junit.Assert;
import org.junit.Test;

import com.dingding.open.achelous.core.parser.Parser;

public class PropertiesParserTest {

    @Test
    public void test_parser_normal() {
        Parser parser = new PropertiesParser();
        try {
            parser.parser();
        } catch (Throwable t) {
            Assert.fail();
        }
    }
}
