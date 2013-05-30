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
package com.github.weikengc.spring.jpdl.activity;

import org.jbpm.api.activity.ActivityBehaviour;
import org.jbpm.api.activity.ActivityExecution;

/**
 *
 * @author weikengc
 */
public class BasicActivityBehaviour implements ActivityBehaviour {

    private ActivityBehaviour deletegateActvityBehaviour;
    public void execute(final ActivityExecution ae) throws Exception {
        deletegateActvityBehaviour.execute(ae);
    }

    public void setDelegate(ActivityBehaviour delegateActivity) {
        deletegateActvityBehaviour = delegateActivity;
    }
}