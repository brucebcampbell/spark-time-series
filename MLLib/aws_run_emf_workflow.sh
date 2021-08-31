
#This script will put data in hdfs and run the MLLIb scala code

hadoop fs -rm -R /user/hadoop/data

hadoop fs -put data /user/hadoop/

#-------Running in local mode
#spark-submit --master local[2] --class com.aloidia.datascience.mllib.MLLibDriver target/MLLibDriver-0.0.1.jar /user/hadoop/data

#-------Running in YARN client mode
spark-submit --master yarn-client --class com.aloidia.datascience.mllib.MLLibDriver target/MLLibDriver-0.0.1.jar /user/hadoop/data

#Example running the example jar
#spark-submit  --class org.apache.spark.examples.SparkPi  --master yarn-client /usr/lib/spark/lib/spark-examples-1.4.1-hadoop2.6.0-amzn-0.jar  100000
