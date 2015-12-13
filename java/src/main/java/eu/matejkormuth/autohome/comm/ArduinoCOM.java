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
package eu.matejkormuth.autohome.comm;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArduinoCOM {

    private static final Logger log = LoggerFactory.getLogger(ArduinoCOM.class);

    private final String portName;
    private SerialPort port;

    public ArduinoCOM(String portName) {
        this.portName = portName;
        port = new SerialPort(portName);
        try {
            port.openPort();
            port.setParams(SerialPort.BAUDRATE_38400, 8, 1, SerialPort.PARITY_NONE);
            // Try to read something.
            tryRead();
        } catch (SerialPortException e) {
            log.error("Can't init serial port!", e);
        }
    }

    public void sendCommand(ArduinoCommand cmd) {
        // Send command.
        try {
            port.writeString(cmd.getCmd() + "\n");
        } catch (SerialPortException e) {
            log.error("Can't write " + cmd.getCmd() + " to serial port!", e);
        }
        // Try to receive all bytes from serial port.
        tryRead();
    }

    private void tryRead() {
        try {
            String str = port.readString();
            log.info("{} >> {}", portName, str);
        } catch (SerialPortException e) {
            log.error("Can't read from serial port!", e);
        }
    }

    private void sleep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isOn = false;

    public void turnLedOn() {
        sendCommand(ArduinoCommand.LED_ON);
    }

    public void turnLedOff() {
        sendCommand(ArduinoCommand.LED_OFF);
    }

    public void fadeLedOn() {
        sendCommand(ArduinoCommand.LED_ON);
        if (!isOn) {
            for (int i = 0; i < 50; i++) {
                sendCommand(ArduinoCommand.LED_INTENSITY_PLUS);
                sleep(500);
            }
        }
        isOn = true;
    }

    public void fadeLedOff() {
        if (isOn) {
            for (int i = 0; i < 50; i++) {
                sendCommand(ArduinoCommand.LED_INTENSITY_MINUS);
                sleep(500);
            }
        }
        sendCommand(ArduinoCommand.LED_OFF);
        isOn = false;
    }
}
