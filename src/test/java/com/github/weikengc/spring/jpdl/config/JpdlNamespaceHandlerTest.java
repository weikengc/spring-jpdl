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

import com.github.weikengc.spring.jpdl.testutil.LocalResources;
import com.google.common.base.Function;
import java.io.File;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.jbpm.api.activity.ActivityBehaviour;
import org.jbpm.api.activity.ActivityExecution;
import org.junit.Ignore;
import static java.util.Arrays.asList;
import static com.google.common.collect.Iterables.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author weikengc
 */
public class JpdlNamespaceHandlerTest {

    private static final Function<File, String> file2AbsolutePath = new Function<File, String>() {
        public String apply(File f) {
            return f.getAbsolutePath();
        }
    };
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private LocalResources resources = LocalResources.forClass(getClass());

    @Test
    @Ignore("Later")
    public void shouldBeAbleToDetectProcessTag() throws Exception {
        newApplicationContextFor(resources.getFile("shouldBeAbleToDetectProcessTag.xml"));
    }

    @Test
    public void shouldBeAbleToTreatJpdlXmlAsSpringXml() throws Exception {
        newApplicationContextFor(resources.getFile("shouldBeAbleToTreatJpdlXmlAsSpringXml.jpdl.xml"));
    }

    @Test
    public void shouldBeAbleToHaveFlowThatOnlyStartsThenEnds() throws Exception {
        newApplicationContextFor(resources.getFile("startsThenEnds.jpdl.xml"));
    }

    @Test
    public void shouldThrowWhenTransitionToEndThatDoesNotExist() throws Exception {
        thrown.expect(cause(message(is("attribute <transition to \"End\" doesn't reference an existing activity name"))));
        newApplicationContextFor(resources.getFile("endDoesNotExist.jpdl.xml"));
    }

    @Test
    public void shouldBeAbleToExecuteProcessFlow() throws Exception {
        ActivityExecution execution = mock(ActivityExecution.class);

        ApplicationContext context = newApplicationContextFor(resources.getFile("executeProcessFlow.jpdl.xml"));
        ActivityBehaviour flow = (ActivityBehaviour) context.getBean("flow");
        flow.execute(execution);

        ActivityBehaviour mockActivity = (ActivityBehaviour) context.getBean("mockActivity");
        verify(mockActivity).execute(execution);
    }

    @Test
    public void shouldBeAbleToExecuteTwoDifferentProcessFlows() throws Exception {
        ApplicationContext context = newApplicationContextFor(resources.getFile("executeTwoProcessFlows.jpdl.xml"));

        ActivityBehaviour flow1 = (ActivityBehaviour) context.getBean("flow1");
        ActivityBehaviour mockActivity1 = (ActivityBehaviour) context.getBean("mockActivity1");
        ActivityBehaviour flow2 = (ActivityBehaviour) context.getBean("flow2");
        ActivityBehaviour mockActivity2 = (ActivityBehaviour) context.getBean("mockActivity2");

        ActivityExecution execution1 = mock(ActivityExecution.class);
        flow1.execute(execution1);
        verify(mockActivity1).execute(execution1);
        verify(mockActivity2, never()).execute(execution1);

        ActivityExecution execution2 = mock(ActivityExecution.class);
        flow2.execute(execution2);
        verify(mockActivity2).execute(execution2);
        verify(mockActivity1, never()).execute(execution2);
    }

    private static ApplicationContext newApplicationContextFor(File... springXmlFiles) {
        String[] springXmlPaths = toArray(transform(asList(springXmlFiles), file2AbsolutePath), String.class);
        return new FileSystemXmlApplicationContext(springXmlPaths);
    }

    private static <T extends Exception> Matcher cause(Matcher<T> causeMatcher) {
        return new FeatureMatcher<Exception, T>(causeMatcher, "cause", "cause") {
            @Override
            protected T featureValueOf(Exception ex) {
                return (T) ex.getCause();
            }
        };
    }

    private static Matcher message(Matcher<String> messageMatcher) {
        return new FeatureMatcher<Exception, String>(messageMatcher, "message", "message") {
            @Override
            protected String featureValueOf(Exception actual) {
                return actual.getMessage();
            }
        };
    }
}