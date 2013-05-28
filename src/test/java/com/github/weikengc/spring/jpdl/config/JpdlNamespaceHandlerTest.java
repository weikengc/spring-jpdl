/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.weikengc.spring.jpdl.config;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author weikengc
 */
public class JpdlNamespaceHandlerTest {

    @Test
    public void shouldBeAbleToDetectProcessTag() {
        newApplicationContextForTestCase("shouldBeAbleToDetectProcessTag");
    }

    @Test
    public void shouldBeAbleToTreatJpdlXmlAsSpringXml() {
        newApplicationContextForTestCase("shouldBeAbleToTreatJpdlXmlAsSpringXml.jpdl");
    }

    private static ClassPathXmlApplicationContext newApplicationContextForTestCase(String testCaseName) {
        return new ClassPathXmlApplicationContext(
                "com/github/weikengc/spring/jpdl/config/JpdlNamespaceHandlerTest-" + testCaseName + ".xml");
    }
}