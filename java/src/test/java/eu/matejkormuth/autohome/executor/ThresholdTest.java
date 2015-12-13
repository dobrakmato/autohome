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

import org.junit.Test;

import static org.junit.Assert.*;

public class ThresholdTest {

    @Test
    public void testIsTrue() throws Exception {
        final boolean[] triggered = {false};
        Threshold t = new Threshold(2);
        t.isTrue(() -> triggered[0] = true);
        t.onStateUpdated(true);
        t.onStateUpdated(true);
        assertTrue("threshold is not true", triggered[0]);
    }

    @Test
    public void testIsFalse() throws Exception {
        final boolean[] triggered = {true};
        Threshold t = new Threshold(2);
        t.isFalse(() -> triggered[0] = false);
        t.onStateUpdated(false);
        t.onStateUpdated(false);
        t.onStateUpdated(false);
        assertFalse("threshold is not false", triggered[0]);
    }

    @Test
    public void testParentNull() throws Exception {
        assertNull("parent should be null", new Threshold(5).parent());
    }

    @Test
    public void testThreshold() throws Exception {
        assertTrue("threshold should be 5", 5 == new Threshold(5).getThreshold());
    }
}