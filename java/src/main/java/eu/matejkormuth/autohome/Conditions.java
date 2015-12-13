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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.function.Supplier;

/**
 * Provides some basic conditions out-of box.
 *
 * @author Matej Kormuth
 * @since 1.0.0
 */
public class Conditions {

    // Logger.
    private static final Logger log = LoggerFactory.getLogger(Conditions.class);
    // Epsilon used in equalTo and notEqualTo conditions.
    private static final double EPSILON = 0.001D;

    // Do not allow instantiation.
    private Conditions() {
    }

    /**
     * Returns whether specified hostname is currently reachable by calling InetAddress.isReachable()
     * with timeout of 1000 ms.
     *
     * @param hostname hostname to test availability of
     * @return condition that returns true if specified hostname is reachable, false otherwise
     */
    public static Condition isReachable(String hostname) {
        return isReachable(hostname, 1000);
    }

    /**
     * Returns whether specified hostname is currently reachable by calling InetAddress.isReachable()
     * with specified timeout in ms.
     *
     * @param hostname hostname to test availability of
     * @return condition that returns true if specified hostname is reachable, false otherwise
     */
    public static Condition isReachable(String hostname, int timeout) {
        return () -> {
            try {
                return InetAddress.getByName(hostname).isReachable(timeout);
            } catch (Exception e) {
                log.error("Error in condition (isReachable): {}!", e);
                return false;
            }
        };
    }

    /**
     * Takes supplier of doubles and checks if supplied number is less than specified max number.
     * <p>
     * This can be used with temperature sensors to turn on cooling / heating.
     *
     * @param max      maximum value
     * @param supplier supplier of values to check
     * @return condition that returns true when supplied number is less than specified number and false otherwise
     */
    public static Condition lessThan(double max, Supplier<Double> supplier) {
        return () -> supplier.get() < max;
    }

    /**
     * Takes supplier of doubles and checks if supplied number is less than or equal specified max number.
     * <p>
     * This can be used with temperature sensors to turn on cooling / heating.
     *
     * @param max      maximum value
     * @param supplier supplier of values to check
     * @return condition that returns true when supplied number is less than or equal to specified number and
     * false otherwise
     */
    public static Condition lessThanOrEqual(double max, Supplier<Double> supplier) {
        return () -> supplier.get() <= max;
    }

    /**
     * Takes supplier of doubles and checks if supplied number is greater than specified min number.
     * <p>
     * This can be used with temperature sensors to turn on cooling / heating.
     *
     * @param min      maximum value
     * @param supplier supplier of values to check
     * @return condition that returns true when supplied number is greater than specified number and false otherwise
     */
    public static Condition greaterThan(double min, Supplier<Double> supplier) {
        return () -> supplier.get() > min;
    }

    /**
     * Takes supplier of doubles and checks if supplied number is greater than or equal specified min number.
     * <p>
     * This can be used with temperature sensors to turn on cooling / heating.
     *
     * @param min      minimal value
     * @param supplier supplier of values to check
     * @return condition that returns true when supplied number is greater than or equal to specified number
     * and false otherwise
     */
    public static Condition greaterThanOrEqual(double min, Supplier<Double> supplier) {
        return () -> supplier.get() >= min;
    }

    /**
     * Takes supplier of doubles and checks if supplied number is equal to specified number.
     * This uses epsilon = 0.001D.
     *
     * @param value    specified number
     * @param supplier supplier of values to check
     * @return condition that returns true when supplied number is equal specified number and false otherwise
     */
    public static Condition equalTo(double value, Supplier<Double> supplier) {
        return () -> Math.abs(supplier.get() - value) < EPSILON;
    }

    /**
     * Takes supplier of doubles and checks if supplied number is not equal to specified number.
     * This uses epsilon = 0.001D.
     *
     * @param value    specified number
     * @param supplier supplier of values to check
     * @return condition that returns true when supplied number is not equal specified number and false otherwise
     */
    public static Condition notEqualTo(double value, Supplier<Double> supplier) {
        return () -> Math.abs(supplier.get() - value) > EPSILON;
    }

    public static Condition isReachablePingWIN(String address) {
        return () -> {
            try {
                Process p = new ProcessBuilder("ping", "-n", "1", "-w", "1000", address).redirectErrorStream(true).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }

                return builder.toString().toLowerCase().contains("time=");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        };
    }
}
