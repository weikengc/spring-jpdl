/*
 * Copyright 2013 weikengc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.weikengc.spring.jpdl.config;

import com.github.weikengc.spring.jpdl.activity.BasicActivityBehaviour;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.w3c.dom.Element;
import com.google.gag.annotation.remark.Facepalm;
import java.util.Arrays;
import org.jbpm.api.JbpmException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.NodeList;

/**
 *
 * @author weikengc
 */
public class JpdlNamespaceHandler extends NamespaceHandlerSupport {

    @Facepalm
    public void init() {
        registerBeanDefinitionParser("process", new ProcessBeanDefinitionParser());
    }

    private static class ProcessBeanDefinitionParser extends AbstractBeanDefinitionParser {

        @Override
        protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
            validateContainsEndState(element);

            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(BasicActivityBehaviour.class);
            System.out.println(Arrays.toString(parserContext.getRegistry().getBeanDefinitionNames()));
            if (parserContext.getRegistry().containsBeanDefinition("mockActivity")) {
                bean.addPropertyReference("delegate", "mockActivity");
            }
            AbstractBeanDefinition bd = bean.getBeanDefinition();
            System.out.println(bd);
            return bd;
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "flow";
        }

        private void validateContainsEndState(Element element) throws JbpmException {
            NodeList nodeList = element.getElementsByTagName("state");
            if (nodeList.getLength() == 0) {
                throw new JbpmException("attribute <transition to \"End\" doesn't reference an existing activity name");
            }
        }
    }
}