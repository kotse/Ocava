/*
 * Copyright © 2017 Ocado (Ocava)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocadotechnology.scenario;

import com.ocadotechnology.notification.NotificationRouter;
import com.ocadotechnology.scenario.StepManager.ExecuteStepExecutionType;

public class TestWhen {

    private final StepManager stepManager;
    private final FrameworkTestSimulationApi simulationApi;
    private final CoreSimulationWhenSteps simulationWhenSteps;

    public TestWhen(StepManager stepManager, FrameworkTestSimulationApi simulationApi, ScenarioNotificationListener listener, NotificationCache notificationCache) {
        this.stepManager = stepManager;
        this.simulationApi = simulationApi;
        this.simulationWhenSteps = new CoreSimulationWhenSteps(stepManager, simulationApi, listener, notificationCache);
    }

    public TestEventWhenSteps testEvent() {
        return new TestEventWhenSteps(stepManager, ExecuteStepExecutionType.ordered(), simulationApi);
    }

    public void simStarts() {
        simulationWhenSteps.starts(TestSimulationStarts.class);
    }

    public static class TestEventWhenSteps {

        private final StepManager stepManager;
        private final ExecuteStepExecutionType executeStepExecutionType;
        private ScenarioSimulationApi simulationHolder;

        public TestEventWhenSteps(StepManager stepManager, ExecuteStepExecutionType executionType, ScenarioSimulationApi simulationHolder) {
            this.stepManager = stepManager;
            this.executeStepExecutionType = executionType;
            this.simulationHolder = simulationHolder;
        }

        protected TestEventWhenSteps withExecutionType(ExecuteStepExecutionType executionType) {
            return new TestEventWhenSteps(stepManager, executionType, simulationHolder);
        }

        public void scheduled(int time, String name) {
            stepManager.add(new ExecuteStep() {
                @Override
                protected void executeStep() {
                    simulationHolder.getEventScheduler().doAt(time,
                        () -> {
                            NotificationRouter.get().broadcast(new TestEventNotification(name));
                        }, "scheduled(" + time + ", \"" + name + "\")");
                }
            }, executeStepExecutionType);
        }
    }
}