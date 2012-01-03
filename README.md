# Rainbow Machine

[![Rainbow Machine](http://farm8.staticflickr.com/7151/6604003423_e85e1afa67_z.jpg)](http://www.therainbowmachine.com)

## About

LPD8806 light painting engine that streams data from a computer to an Arduino at a high rate to display high resolution images. Streaming overcomes the space limitations of the Arduino.

We mounted our LED strip on a bar that rotates 180 degrees orthogonal to the ground, making it devilishly simple to make a rainbow. We have people sit in front of it and take their photo. [More...](http://www.therainbowmachine.com/)

## Installation Instructions

1. Buy [this](http://www.adafruit.com/products/306) from Adafruit
2. Install [LPD8806 Arduino library](https://github.com/adafruit/LPD8806)
3. Grab code and load in Eclipse
4. Convert image files to rainbow data files using `./process.sh input.jpg output.dat`
5. Send rainbow data files to the LPD8806 using `./engine.sh file.dat`

## How it works

A protocol is defined to communicate pixel colours from the computer to the Arduino frame by frame. Combined with an Arduino Uno, 115200 serial baud rate, and Adafruit's SPI high speed library for the LPD8806, rates upwards of 40 FPS have be observed.

The protocol is presently one-way. The computer streams the data without regard to how the Arduino handles it. Thus, it is the implementor's job to find the proper combination of baud rate and the computer stream rate to find a suitable rate. If the rate is too fast, the Arduino will not be able to properly handle the stream. See [this discussion](http://www.arduino.cc/cgi-bin/yabb2/YaBB.pl?num=1177477050).


