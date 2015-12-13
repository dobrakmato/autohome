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
import eu.matejkormuth.autohome.api.StateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents connection between Condition and its listeners - Runnables / method references.
 *
 * @author Matej Kormuth
 * @since 1.0.0
 */
public final class When {

    // Logger.
    private static final Logger log = LoggerFactory.getLogger(When.class);

    // Condition used in this when.
    final Condition condition;
    // List of runnables that should be called when condition returns true.
    private final List<Runnable> isTrue;
    // List of runnables that should be called when condition returns false.
    private final List<Runnable> isFalse;

    // Only allow Executor to make instances of When.
    When(Condition applies) {
        this.condition = applies;
        isFalse = new ArrayList<>(2);
        isTrue = new ArrayList<>(2);
    }

    // Used to notify all listeners about 'true' state. Called from Executor.
    void notifyTrue() {
        for (int i = 0; i < isTrue.size(); i++) {
            try {
                isTrue.get(i).run();
            } catch (Exception e) {
                log.error("Can't execute {} because {}!", isTrue.get(i), e);
            }
        }
    }

    // Used to notify all listeners about 'false' state. Called from Executor.
    void notifyFalse() {
        for (int i = 0; i < isFalse.size(); i++) {
            try {
                isFalse.get(i).run();
            } catch (Exception e) {
                log.error("Can't execute {} because {}!", isFalse.get(i), e);
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
    public When isTrue(Runnable method) {
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
    public When isFalse(Runnable method) {
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
    public When stateChanged(StateProcessor stateProcessor) {
        isTrue.add(() -> stateProcessor.onStateUpdated(true));
        isFalse.add(() -> stateProcessor.onStateUpdated(false));
        return this;
    }

    /**
     * Creates Threshold that will be used to smooth out results for next isFalse(), isTrue() and updates()
     * calls until the call parent() is made.
     *
     * @param threshold minimum number of same state updates to allow state update to pass further
     * @return threshold object
     * @see Threshold
     */
    public Threshold threshold(int threshold) {
        Threshold threshold1 = new Threshold(threshold);
        threshold1.parent = this;
        this.stateChanged(threshold1);
        return threshold1;
    }

    @Override
    public String toString() {
        return "When{" +
                "condition=" + condition +
                ", isTrue=" + isTrue +
                ", isFalse=" + isFalse +
                '}';
    }
}
