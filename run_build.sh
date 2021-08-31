#!/bin/bash

cd /mnt/data/spark-time-series


#export JAVA_HOME=/usr/lib/jvm/java-1.8.0

cd /mnt/data/spark-time-series/
rm -rf spark-timeseries
git clone https://github.com/sryza/spark-timeseries
cd spark-timeseries
mvn package -DskipTests
cd ../

rm -rf makina
git clone https://github.com/jrachiele/makina
cd makina
gradle build
cd ../

rm -rf smile
git clone https://github.com/haifengl/smile
cd smile
sbt clean compile test package
cd ../

rm -rf java-timeseries
git clone https://github.com/jrachiele/java-timeseries
cd java-timeseries
gradle build
cd ../

cd TSFeatures
mvn -DskipTests clean package

cd ../DSApp
mvn -DskipTests clean package

cd ../MLLib
mvn -DskipTests clean package

cd ../NetlibDriver
mvn -DskipTests clean package

cd ../TimeSeriesAnalytics
mvn -DskipTests clean package


