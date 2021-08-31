
This project contians functionality for distributed time series analytics 
using Apache Spark, MLLib, and spark-timeseries


Steps to install spark-timeseries

cd ~/SparkDev/klTimeSeriesAnalytics
rm -rf spark-timeseries/ # I keep a copy in the git repo for development purposes
git clone https://github.com/cloudera/spark-timeseries
cd spark-timeseries/
mvn package
cd ~/SparkDev/klTimeSeriesAnalytics
mkdir src/main/resources/
cp ~/SparkDev/spark-timeseries/target/sparktimeseries-0.0.1.jar src/main/resources/


# This should work - but but it does not 
mvn install:install-file  \
-Dfile=  /home/hadoop/SparkDev/spark-timeseries/target/sparktimeseries-0.0.1.jar \
-DgroupId=com.cloudera.datascience \
-DartifactId=sparktimeseries \
-Dversion=0.0.1 \
-Dpackaging=jar \
-DgeneratePom=true

#Alternative
<dependency>
    <groupId>com.cloudera.datascience</groupId>
    <artifactId>sparktimeseries</artifactId>
    <version>0.0.1</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/src/main/resources/sparktimeseries-0.0.1.jar</systemPath>
</dependency>

To add  
 spark-shell --jars target/sparktimeseries-0.0.1-jar-with-dependencies.jar