#!/bin/bash
RUN_PID=`ps -C 'java -Xms256m -Xmx512m -Djava.awt.headless=true ${pkg}.${pnm}Launcher' -f | grep 'java -Xms256m -Xmx512m -Djava.awt.headless=true ${pkg}.${pnm}Launcher' | awk '{print $2}'`
echo 'Find and Kill, ${pnm}Launcher PID: '$RUN_PID
kill $RUN_PID