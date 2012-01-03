#!/bin/bash

java -Djava.library.path=./lib -cp ./bin:lib/RXTXcomm.jar dev.boxy.rainbow.RainbowEngine $1
