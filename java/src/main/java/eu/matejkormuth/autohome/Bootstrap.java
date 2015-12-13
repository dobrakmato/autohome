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

import eu.matejkormuth.autohome.comm.ArduinoCOM;
import eu.matejkormuth.autohome.executor.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used for application boot-up process.
 */
public class Bootstrap {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    /**
     * Here you can prepare all your devices and logic / connections between them.
     *
     * @param executor executor used to create logic
     */
    private static void prepare(Executor executor) {
        // Create things.
        ArduinoCOM arduino = new ArduinoCOM("COM3");

        // When my mobile is reachable turn on the lights.
        executor.when(Conditions.isReachablePingWIN("192.168.0.14"))
                .threshold(2)
                    .isTrue(arduino::turnLedOn)
                    .isFalse(arduino::turnLedOff);
    }

    // Entry point.
    public static void main(String[] args) {
        // Create basic executor.
        Executor executor = new Executor(1);

        // Prepare all devices and logic / connections between them.
        prepare(executor);

        // Start console.

    }
}
