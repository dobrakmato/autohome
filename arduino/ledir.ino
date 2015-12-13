
/**
 * Pin the IR LED is connected to.
 */
#define IRLEDpin  12       
/**       
 * Length of the carrier bit in microseconds
 */
#define BITtime   562            

/*
 * Here are some constants of IR codes from this cool website:
 * http://i1.wp.com/woodsgood.ca/projects/wp-content/uploads/24keyIRRemoteCodes.jpg
 */
#define    ONcode 0xC1F7C03F  // Turn on
#define   OFFcode 0xC1F740BF  // Turn off
#define WHITEcode 0xC1F7E01F  // Color: white
#define   REDcode 0xC1F720DF  // Color: red
#define  BLUEcode 0xC1F7609F  // Color: blue
#define GREENcode 0xC1F7A05F  // Color: green
#define INTEPcode 0xC1F700FF  // Intensity plus
#define INTEMcode 0xC1F7807F  // Intensity minus

// Ouput the 38KHz carrier frequency for the required time in microseconds
// This is timing critial and just do-able on an Arduino using the standard I/O functions.
// If you are using interrupts, ensure they disabled for the duration.
void IRcarrier(unsigned int IRtimemicroseconds)
{
  for(int i=0; i < (IRtimemicroseconds / 26); i++)
    {
    digitalWrite(IRLEDpin, HIGH);   //turn on the IR LED
    //NOTE: digitalWrite takes about 3.5us to execute, so we need to factor that into the timing.
    delayMicroseconds(9);          //delay for 13us (9us + digitalWrite), half the carrier frequnecy
    digitalWrite(IRLEDpin, LOW);    //turn off the IR LED
    delayMicroseconds(9);          //delay for 13us (9us + digitalWrite), half the carrier frequnecy
    }
}

/**
 * Sends the IR code in 4 byte NEC format
 */
void IRsendCode(unsigned long code)
{
  //send the leading pulse
  IRcarrier(9000);            //9ms of carrier
  delayMicroseconds(4500);    //4.5ms of silence
  
  //send the user defined 4 byte/32bit code
  for (int i=0; i<32; i++)            //send all 4 bytes or 32 bits
    {
    IRcarrier(BITtime);               //turn on the carrier for one bit time
    if (code & 0x80000000)            //get the current bit by masking all but the MSB
      delayMicroseconds(3 * BITtime); //a HIGH is 3 bit time periods
    else
      delayMicroseconds(BITtime);     //a LOW is only 1 bit time period
     code<<=1;                        //shift to the next bit for this byte
    }
  IRcarrier(BITtime);                 //send a single STOP bit.
}

/**
 * Sends the IR code three times to be sure that receiver
 * receives it.
 */
void IRsendSafe(unsigned long code) 
{
  for(int i = 0; i < 3; i++) 
  {
    IRsendCode(code);                 
  }
}

String cmdBuffer = "";         // a string to hold incoming data
boolean cmdComplete = false;  // whether the string is complete

void serialRead() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    if (inChar == '\n') {
      cmdComplete = true;
    } else {
      cmdBuffer += inChar;
    }
  }
}

/**
 * Setup the board.
 */
void setup()
{
  // Initialize serial connection for incoming commands.
  Serial.begin(38400);
  // Reserve 32 bytes for the inputString.
  cmdBuffer.reserve(32);
  
  // Setup the IR LED.
  pinMode(IRLEDpin, OUTPUT);
  digitalWrite(IRLEDpin, LOW);    //turn off IR LED to start

  // Turn off work indicator led.
  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);

  Serial.print("Ready!");
}

/**
 * Executed when the whole command is read from serial input.
 */
void onCommand() 
{
  if(cmdBuffer.equals("LED_ON")) {
    Serial.print("Sending ON code...");
    IRsendSafe(ONcode);
  } else if(cmdBuffer.equals("LED_OFF")) {
    Serial.print("Sending OFF code...");
    IRsendSafe(OFFcode);
  } else if(cmdBuffer.equals("LED_WHITE")) {
    Serial.print("Sending WHITE code...");
    IRsendSafe(WHITEcode);
  } else if(cmdBuffer.equals("LED_IP")) {
    Serial.print("Sending INTEP code...");
    IRsendSafe(INTEPcode);
  } else if(cmdBuffer.equals("LED_IM")) {
    Serial.print("Sending INTEM code...");
    IRsendSafe(INTEMcode);
  } else {
    Serial.print("Unsupported command: " + cmdBuffer);
  }
}

/**
 * Actual loop program.
 */
void loop()                      
{
  // Read command from serial port.
  serialRead();

  // Dispatch the command if complete.
  if(cmdComplete) {
    // Dispatch command.
    digitalWrite(13, HIGH);
    onCommand();
    digitalWrite(13, LOW);
    // Clear executed command and flag.
    cmdBuffer = "";
    cmdComplete = false;
  }
}
