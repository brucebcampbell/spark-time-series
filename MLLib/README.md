This is a demo project 

$SPARK_HOME/bin/spark-submit \
--master yarn-client \
--num-executors 3 \
 --driver-memory 4g \
--executor-memory 2g \
--executor-cores 1 \
--class com.aloidia.spark.asd.scala.WordCount \
  ./target/asd-0.0.1.jar \
  ./README.md ./wordcounts


$SPARK_HOME/bin/spark-submit \
--master local[2] \
--class com.aloidia.spark.asd.scala.WordCount \
  ./target/asd-0.0.1.jar \
  ./README.md ./wordcounts
