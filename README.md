autohome
-------------------------

Make the light turn on automatically when you get home!

(This is just a demonstration of possibility.)

# About

This project consist of two things:
 - Arduino board
 - Controller application

You connect your arduino board to a computer running controller application. Then the controller app periodically checks
if your mobile phone is connected to your home wifi network and turn the LED lights on using IR Led on thw arduino board
when your phone is connected to wifi.

This is **not** a working solution! This is proof of concept and a project made for fun. You can however fork the repo
and try to do more cool stuff with it!

# Arduino

You can use Arduino IDE to build and flash program in `arduino` folder to your board.

Arduino IDE: https://www.arduino.cc/en/Main/Software

# Controller application

To compile the controller application, you need to have:

 - Java 8+
 - Maven 3 (https://maven.apache.org/download.cgi)

Install your arduino board drivers and connect it to the computer. Mark the name of serial port that the arudino board
is connected to. Change the port name in `Bootstrap.java` to match with your port.

You can use `Bootstrap.java` to make more changes and to configure more stuff to happen.

Execute `mvn package` to build application in `java` directory.

Then run `java -jar autohome-X.Y.Z-jar-with-dependencies.jar` to start application. Before running the controller
application, make sure your arduino board is connected.