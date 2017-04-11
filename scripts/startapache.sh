#!/bin/bash
killall -9 java
export JAVA_HOME=/opt/java/jdk1.8.0_45
export PATH=/opt/gradle-2.9/bin:$PATH
sudo su
cd /var/www/html/
YMD="$( date "+%Y-%m-%d" )"
nohup ./gradlew --stacktrace > /var/log/pakka/"pakka.${YMD}.log" 2>&1 &