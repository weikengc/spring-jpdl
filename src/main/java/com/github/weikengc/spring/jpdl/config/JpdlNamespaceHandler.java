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
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
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
            Element firstTransition = DomUtils.getChildElementByTagName(startNode, "transition");
            log.debug("Processing start node");
            if (firstTransition instanceof Element) {
                Element transition = (Element) firstTransition;
                log.debug("Evaluating transition");
                String transitionTo = transition.getAttribute("to");
                log.debug("Start node transitions to '{}'", transitionTo);

                Element transitionTarget = findCustomNodeWithName(transitionTo, processElement);
                if (transitionTarget != null) {
                    String expressionName = transitionTarget.getAttribute("expr");
                    if (expressionName != null) {
                        String beanName = expressionName.substring(2, expressionName.length() - 1);
                        log.debug("Custom node [{}] referring to bean '{}'", transitionTo, beanName);

                        if (parserContext.getRegistry().containsBeanDefinition(beanName)) {
                            bean.addPropertyReference("delegate", beanName);
                        }
                    }
                }
            }
            AbstractBeanDefinition bd = bean.getBeanDefinition();
            return bd;
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {

            String value = element.getAttribute("key");
            if (!(value == null || value.isEmpty())) {
                return value;
            }

            return "default";
        }

        private void validateContainsEndState(Element element) throws JbpmException {
            NodeList nodeList = element.getElementsByTagName("state");
            if (nodeList.getLength() == 0) {
                throw new JbpmException("attribute <transition to \"End\" doesn't reference an existing activity name");
            }
        }

        /**
         * Looks for Element with the given name e.g. <custom
         * name="${nameValue}"/>.
         *
         * @param nameValue
         * @param parentElement
         * @return
         */
        private Element findCustomNodeWithName(String nameValue, Element parentElement) {
            List<Element> customNodes = DomUtils.getChildElementsByTagName(parentElement, "custom");
            log.debug("Custom elements found: {}", customNodes);
            for (Element customNode : customNodes) {
                if (customNode.hasAttribute("name")) {
                    return customNode;
                }
            }
            return null;
        }
    }
}