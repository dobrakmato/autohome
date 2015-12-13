/**
 * AutoHome - Application for intelligent automatic house management.
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.matejkormuth.autohome.executor;

import eu.matejkormuth.autohome.api.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main class of this library. It is used for pairing Conditions with condition state processors (switches, etc...).
 * It also plans and executes condition pairs periodically.
 *
 * @author Matej Kormuth
 * @since 1.0.0
 */
public final class Executor {

    // Logger.
    private static final Logger log = LoggerFactory.getLogger(Executor.class);
    // Internal scheduled executor.
    private final ScheduledExecutorService executorService;

    // List of all registered whens to check.
    private final List<When> whens;
    // Interval in seconds, how often are all whens checked.
    private final int whenCheckInterval = 7;

    /**
     * Creates new instance of Executor with 2 threads.
     */
    public Executor() {
        this(2);
    }

    /**
     * Creates new instance of Executor with specified amount of threads.
     *
     * @param threads number of threads to use for checking and executing stuff
     */
    public Executor(int threads) {
        executorService = Executors.newScheduledThreadPool(threads);
        whens = new ArrayList<>();

        // Schedule whens checking periodically each ${whenCheckInterval} seconds.
        executorService.scheduleAtFixedRate(this::checkWhens, 0, whenCheckInterval, TimeUnit.SECONDS);
    }

    // Checks all whens and notifies all listeners.
    private void checkWhens() {
        When when;
        long startTime, total;
        for (int i = 0; i < whens.size(); i++) {
            when = whens.get(i);
            // Profiling to find blocking (long operations).
            startTime = System.nanoTime();
            boolean result;
            try {
                // Get the result from condition.
                result = when.condition.check();
            } catch (Exception e) {
                log.error("Can't check condition of {} because {}!", when, e);
                // Skip to next 'When'.
                continue;
            }

            // Notify al handlers.
            if (result) {
                when.notifyTrue();
            } else {
                when.notifyFalse();
            }
            total = (System.nanoTime() - startTime) / 1000000;
            if (total > whenCheckInterval * 1000 / whens.size() / 2) {
                // Log warning message about execution time.
                log.warn("When {} is taking more time then it should. Last processing took {} ms, "
                                + "but AVG planned execution time for one When is {}."
                                + " That {}% of planned execution time.",
                        total, whenCheckInterval * 1000 / whens.size(),
                        (int) (total / whenCheckInterval * 1000 / whens.size() * 100));
            }
        }
    }

    /**
     * Creates new When. It is used for pairing conditions with state processors.
     *
     * @param condition condition that provides state
     * @return newly created when connection
     */
    public When when(Condition condition) {
        When when = new When(condition);
        this.whens.add(when);
        return when;
    }


}
