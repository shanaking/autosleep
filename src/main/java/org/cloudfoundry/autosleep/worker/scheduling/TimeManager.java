/*
 * Autosleep
 * Copyright (C) 2016 Orange
 * Authors: Benjamin Einaudi   benjamin.einaudi@orange.com
 *          Arnaud Ruffin      arnaud.ruffin@orange.com
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

package org.cloudfoundry.autosleep.worker.scheduling;

import org.cloudfoundry.autosleep.config.Config;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TimeManager {

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Config.NB_THREAD_FOR_TASK);

    public ScheduledFuture<?> schedule(Runnable command,
                                       Duration duration) {
        return scheduler.schedule(command, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void sleep(Duration duration) throws InterruptedException {
        Thread.sleep(duration.toMillis());
    }

}
