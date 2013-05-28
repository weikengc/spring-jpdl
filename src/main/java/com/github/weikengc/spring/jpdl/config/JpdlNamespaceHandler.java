package com.github.weikengc.spring.jpdl.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 *
 * @author weikengc
 */
public class JpdlNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("process", new BeanDefinitionParser() {
            public BeanDefinition parse(Element element, ParserContext parserContext) {
                return null;
            }
        });
    }
}