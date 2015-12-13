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
package eu.matejkormuth.autohome;

import eu.matejkormuth.autohome.api.Condition;
import org.junit.Ignore;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionsTest {

    /*
     *  ======================================================================
     *      THIS TEST TAKES TOO LONG TO COMPLETE AND IT OFTEN FALSE FAIL.
     *  ======================================================================
     */
    @Test
    @Ignore
    public void testIsReachable() throws Exception {
        Condition condition = Conditions.isReachable("127.0.0.1");
        assertTrue("localhost is not reachable", condition.check());

        Condition condition2 = Conditions.isReachable("1.1.1.1");
        assertFalse("1.1.1.1 is reachable", condition2.check());
    }

    /*
     *  ======================================================================
     *      THIS TEST TAKES TOO LONG TO COMPLETE AND IT OFTEN FALSE FAIL.
     *  ======================================================================
     */
    @Test
    @Ignore
    public void testIsReachableWithTimeout() throws Exception {
        Condition condition = Conditions.isReachable("127.0.0.1", 2500);
        assertTrue("localhost is not reachable", condition.check());

        Condition condition2 = Conditions.isReachable("1.1.1.1", 2500);
        assertFalse("1.1.1.1 is reachable", condition2.check());
    }


    @Test
    public void testLessThan() throws Exception {
        Supplier<Double> doubleSupplier = new Supplier<Double>() {
            public int i = 0;
            public double[] nums = new double[]{2, 10, 8, 5};

            @Override
            public Double get() {
                return nums[i++];
            }
        };

        Condition condition = Conditions.lessThan(5, doubleSupplier);

        assertTrue(condition.check()); // 2
        assertFalse(condition.check()); // 10
        assertFalse(condition.check()); // 8
        assertFalse(condition.check()); // 5
    }

    @Test
    public void testLessThanOrEqual() throws Exception {
        Supplier<Double> doubleSupplier = new Supplier<Double>() {
            public int i = 0;
            public double[] nums = new double[]{2, 10, 8, 5};

            @Override
            public Double get() {
                return nums[i++];
            }
        };

        Condition condition = Conditions.lessThanOrEqual(5, doubleSupplier);

        assertTrue(condition.check()); // 2
        assertFalse(condition.check()); // 10
        assertFalse(condition.check()); // 8
        assertTrue(condition.check()); // 5
    }

    @Test
    public void testGreaterThan() throws Exception {
        Supplier<Double> doubleSupplier = new Supplier<Double>() {
            public int i = 0;
            public double[] nums = new double[]{2, 10, 8, 5};

            @Override
            public Double get() {
                return nums[i++];
            }
        };

        Condition condition = Conditions.greaterThan(5, doubleSupplier);

        assertFalse(condition.check()); // 2
        assertTrue(condition.check()); // 10
        assertTrue(condition.check()); // 8
        assertFalse(condition.check()); // 5
    }

    @Test
    public void testGreaterThanOrEqual() throws Exception {
        Supplier<Double> doubleSupplier = new Supplier<Double>() {
            public int i = 0;
            public double[] nums = new double[]{2, 10, 8, 5};

            @Override
            public Double get() {
                return nums[i++];
            }
        };

        Condition condition = Conditions.greaterThanOrEqual(5, doubleSupplier);

        assertFalse(condition.check()); // 2
        assertTrue(condition.check()); // 10
        assertTrue(condition.check()); // 8
        assertTrue(condition.check()); // 5
    }

    @Test
    public void testEqualTo() throws Exception {
        Supplier<Double> doubleSupplier = new Supplier<Double>() {
            public int i = 0;
            public double[] nums = new double[]{2, 5, 8, 5};

            @Override
            public Double get() {
                return nums[i++];
            }
        };

        Condition condition = Conditions.equalTo(5, doubleSupplier);

        assertFalse(condition.check()); // 2
        assertTrue(condition.check()); // 5
        assertFalse(condition.check()); // 8
        assertTrue(condition.check()); // 5
    }

    @Test
    public void testNotEqualTo() throws Exception {
        Supplier<Double> doubleSupplier = new Supplier<Double>() {
            public int i = 0;
            public double[] nums = new double[]{2, 5, 8, 5};

            @Override
            public Double get() {
                return nums[i++];
            }
        };

        Condition condition = Conditions.notEqualTo(5, doubleSupplier);

        assertTrue(condition.check()); // 2
        assertFalse(condition.check()); // 5
        assertTrue(condition.check()); // 8
        assertFalse(condition.check()); // 5;
    }
}