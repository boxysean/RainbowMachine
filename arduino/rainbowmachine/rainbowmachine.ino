#include "LPD8806.h"
#include <SPI.h>

// Define the number of LEDs on your strip

#define LEDS_START 0
#define LEDS_END 78
#define LEDS (LEDS_END - LEDS_START)

// The higher this is, the dimmer the LEDs become

#define DIMMER 0

// The higher this is, the faster the serial transfer rate is

#define BAUD_RATE 115200

LPD8806 strip = LPD8806(LEDS_END);

void setup() {
  Serial.begin(BAUD_RATE);
  strip.begin();
}

#define NONE ((byte) 0)
#define LINE ((byte) 1)
#define FLUSH ((byte) 2)
#define LINE_LENGTH ((byte) 3)

char line[LEDS_END * 3];
int lineIdx = 0;
int lineLength = 0;

void loop() {
  if (Serial.available()) {
      int b = Serial.read();
      
      Serial.print("i received "); Serial.println(b); Serial.flush();
      
      switch (b) {
      case LINE:
        for (int i = 0; i < lineLength; i++) {
          while (!Serial.available());
          int r = Serial.read();
          line[i] = (char) r;
        }
        
        // DEBUG:
        // Serial.print("set to ");
        // for (int i = 0; i < lineLength; i++) {
        //   Serial.print(line[i]);
        // }
        // Serial.write("\n"); Serial.flush();
        
        break;
        
      case FLUSH:
        flushLine();
        break;
        
      case LINE_LENGTH:
        while (!Serial.available());
        lineLength = Serial.read();
        Serial.print("line length updated to "); Serial.println(lineLength); Serial.flush();
      }
  }
}

void flushLine() {
  // DEBUG:
  // for (int i = 0; i < lineLength; i++) {
  //   Serial.write(line[i]);
  // }
  // Serial.println();
  // Serial.flush();
  
  for (int i = 0; i < lineLength/3; i++) {
    int r = line[3*i] & 0xFF;
    int g = line[(3*i)+1] & 0xFF;
    int b = line[(3*i)+2] & 0xFF;
    
    strip.setPixelColor(LEDS_END - 1 - i, strip.Color(r>>DIMMER, g>>DIMMER, b>>DIMMER));
    
    // DEBUG:
    // Serial.print("setting colour ");
    // Serial.print(r, DEC);
    // Serial.print(" ");
    // Serial.print(g, DEC);
    // Serial.print(" ");
    // Serial.println(b, DEC);
  }
  
  // DEBUG:
  // Serial.println("flushing (showing)");
  // Serial.flush();
  
  strip.show();
  
  // DEBUG:
  // Serial.println("flushing (done)");
  // Serial.flush();
  
}

