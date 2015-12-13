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

import eu.matejkormuth.autohome.api.StateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Threshold is used for smoothing out state changes. It is useful when it takes time for Condition
 * to stabilize or when Condition gives incorrect results from time to time.
 * <p>
 * For example when network availability is used as condition, it can sometime provide incorrect
 * results (because of short internet outage). Using threshold this can be smoothed out. When using
 * threshold of 3, then at least 3 state updates are needed for state passing further.
 *
 * @author Matej Kormuth
 * @since 1.0.0
 */
public final class Threshold implements StateProcessor {

    private static final Logger log = LoggerFactory.getLogger(Threshold.class);

    // Threshold that should be reached before changing state.
    private final int threshold;

    // Current true state score.
    private int currentTrueScore;
    // Current false state score.
    private int currentFalseScore;

    // List of runnables that should be called when condition returns true.
    private final List<Runnable> isTrue;
    // List of runnables that should be called when condition returns false.
    private final List<Runnable> isFalse;

    // Parent When if available.
    When parent = null;

    Threshold(int threshold) {
        this.threshold = threshold;
        isFalse = new ArrayList<>(2);
        isTrue = new ArrayList<>(2);
    }

    @Override
    public void onStateUpdated(boolean newState) {
        if (newState) {
            currentTrueScore++;
            currentFalseScore = 0;

            if (currentTrueScore >= threshold) {
                triggerTrue();
            }
        } else {
            currentFalseScore++;
            currentTrueScore = 0;

            if (currentFalseScore >= threshold) {
                triggerFalse();
            }
        }
    }

    /**
     * Adds specified Runnable or method (using method reference) to list of true state listeners.
     * <p>
     * Method gets executed many times even when state of condition isn't changed!
     *
     * @param method method or Runnable that should be executed when condition state is true
     * @return instance of itself for fluent method chaining
     */
    public Threshold isTrue(Runnable method) {
        isTrue.add(method);
        return this;
    }

    /**
     * Adds specified Runnable or method (using method reference) to list of false state listeners.
     * <p>
     * Method gets executed many times even when state of condition isn't changed!
     *
     * @param method method or Runnable that should be executed when condition state is false
     * @return instance of itself for fluent method chaining
     */
    public Threshold isFalse(Runnable method) {
        isFalse.add(method);
        return this;
    }

    /**
     * Adds specified StateProcessor to list of listeners.
     * <p>
     * Method gets executed many times even when state of condition isn't changed!
     *
     * @param stateProcessor state processor that is captable of processing / consuming true or false values
     * @return instance of itself for fluent method chaining
     */
    public Threshold stateChanged(StateProcessor stateProcessor) {
        isTrue.add(() -> stateProcessor.onStateUpdated(true));
        isFalse.add(() -> stateProcessor.onStateUpdated(false));
        return this;
    }


    /**
     * Returns parent When (if any) used to create this Threshold.
     *
     * @return parent When if available, null otherwise
     */
    public When parent() {
        return this.parent;
    }

    // Used to notify all listeners about 'true' state. Called from Executor.
    void triggerTrue() {
        for (int i = 0; i < isTrue.size(); i++) {
            try {
                isTrue.get(i).run();
            } catch (Exception e) {
                log.error("Can't execute {} because {}!", isTrue.get(i), e);
            }
        }
    }

    // Used to notify all listeners about 'false' state. Called from Executor.
    void triggerFalse() {
        for (int i = 0; i < isFalse.size(); i++) {
            try {
                isFalse.get(i).run();
            } catch (Exception e) {
                log.error("Can't execute {} because {}!", isFalse.get(i), e);
            }
        }
    }

    public int getThreshold() {
        return threshold;
    }
}
