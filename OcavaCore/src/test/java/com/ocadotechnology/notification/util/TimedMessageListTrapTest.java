/*
 * Copyright © 2017-2019 Ocado (Ocava)
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
package com.ocadotechnology.notification.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.ocadotechnology.notification.Notification;
import com.ocadotechnology.notification.NotificationRouter;
import com.ocadotechnology.time.TimeProvider;

public class TimedMessageListTrapTest {
    private static class TestNotification  implements Notification {}

    public class MockTimeProvider implements TimeProvider {
        private double time = 0.0;

        @Override
        public double getTime() {
            return time++;
        }
    }

    @AfterEach
    public void after() {
        NotificationRouter.get().clearAllHandlers();
    }

    @Test
    public void getCapturedNotifications() {
        TimedMessageListTrap<TestNotification> trap = new TimedMessageListTrap<>(TestNotification.class, new MockTimeProvider());
        NotificationRouter.get().broadcast(new TestNotification());
        NotificationRouter.get().broadcast(new TestNotification());
        NotificationRouter.get().broadcast(new TestNotification());
        Assertions.assertEquals(3, trap.getCapturedNotifications().size(), "Expected 3 Notifications");
        Assertions.assertEquals(0.0, trap.getCapturedNotifications().get(0).time, 0.1);
        Assertions.assertEquals(1.0, trap.getCapturedNotifications().get(1).time, 0.1);
        Assertions.assertEquals(2.0, trap.getCapturedNotifications().get(2).time, 0.1);
    }
}