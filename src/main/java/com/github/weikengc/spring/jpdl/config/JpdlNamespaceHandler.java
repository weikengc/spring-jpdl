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
import java.util.List;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.w3c.dom.Element;
import org.jbpm.api.JbpmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.NodeList;

/**
 *
 * @author weikengc
 */
public class JpdlNamespaceHandler extends NamespaceHandlerSupport {

    private Logger log = LoggerFactory.getLogger(getClass());

    public void init() {
        registerBeanDefinitionParser("process", new ProcessBeanDefinitionParser());
    }

    private class ProcessBeanDefinitionParser extends AbstractBeanDefinitionParser {

        @Override
        protected AbstractBeanDefinition parseInternal(Element processElement, ParserContext parserContext) {
            validateContainsEndState(processElement);

            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(BasicActivityBehaviour.class);

            Element startNode = DomUtils.getChildElementByTagName(processElement, "start");
            log.debug("Processing start node");
            String transition = DomUtils.getChildElementByTagName(startNode, "transition").getAttribute("to");
            log.debug("Start node transitions to '{}'", transition);
            searchAndRegisterProcessNode(transition, processElement, parserContext.getRegistry(), bean, parserContext.getReaderContext());
            return bean.getBeanDefinition();
        }

        @Override
        protected String resolveId(Element process, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            if (process.hasAttribute("key")) {
                return process.getAttribute("key");
            }
            throw new BeanDefinitionStoreException("<process> must contains key attribute.");
        }

        private void validateContainsEndState(Element element) throws JbpmException {
            NodeList nodeList = element.getElementsByTagName("state");
            if (nodeList.getLength() == 0) {
                throw new JbpmException("attribute <transition to \"End\" doesn't reference an existing activity name");
            }
        }

        /**
         * @param customNodeName
         * @param processElement
         * @param beanDefinitionRegistry
         * @param bean
         */
        private void searchAndRegisterProcessNode(String customNodeName,
                Element processElement, BeanDefinitionRegistry beanDefinitionRegistry,
                BeanDefinitionBuilder bean, XmlReaderContext context) {
            
            Element customNode = findCustomNode(processElement);
            if (customNode == null) {
                return;
            }
            String beanName = extractExpressionFrom(customNode);
            if (beanName == null) {
                return;
            }

            log.debug("Custom node '{}' is referring to bean '{}'", customNodeName, beanName);

            if (beanDefinitionRegistry.containsBeanDefinition(beanName)) {
                bean.addPropertyReference("delegate", beanName);
            } else {
                String errorMessage = String.format("Custom node '%s' referring to non-existent bean [%s]. Line %s", customNodeName, beanName);
                context.error(errorMessage, customNode);
            }
        }

        private String extractExpressionFrom(Element customNode) {
            String rawExpression = customNode.getAttribute("expr"); // #{expression}
            return rawExpression == null ? null : rawExpression.substring(2, rawExpression.length() - 1);
        }

        /**
         * Looks for Element with the given name e.g. <custom
         * name="${nameValue}"/>.
         *
         * @param nameValue
         * @param parentElement
         * @return
         */
        private Element findCustomNode(Element parentElement) {
            List<Element> customNodes = DomUtils.getChildElementsByTagName(parentElement, "custom");
            log.debug("Custom elements found: {}", customNodes);
            return customNodes.isEmpty() ? null : customNodes.get(0);
        }
    }
}