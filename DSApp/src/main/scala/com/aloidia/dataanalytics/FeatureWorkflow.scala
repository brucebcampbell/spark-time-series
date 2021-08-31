package com.aloidia.datascience

import com.aloidia.datascience.univariatefeatures.{SampleEntropyFeature, FeatureFactory, VarFeature}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SparkSession, _}
import org.apache.spark.sql.types._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.types._


class FeatureWorkflow {

  case class FeatureRecord (LevelsFeature: Double,
    SampleEntropy: Double,
    ThirdQuartile: Double,
    Variance: Double,
    NinetyFifthPercentile: Double,
    SampleCount: Double,
    Skewness: Double,
    Sum: Double,
    ModeFeature: Double,
    Kurtosis: Double,
    StandardDeviation: Double,
    MedianFeature: Double,
    FifthPercentile: Double,
    Max: Double,
    Min: Double,
    Mean: Double,
    ApproximateEntropy: Double) extends Serializable

  def runV3(spark:SparkSession):Unit ={

    //Typically we want 2-4 partitions for each CPU in your cluster.
    //Normally, Spark tries to set the number of partitions automatically based on the cluster.
    //m2.4xlarge  8  68.4 -------> n *8 *4 * Factor

    val factor = 4;
    val cores= 16
    val typicalPartitions=4
    val nodes = 6 //TODO SCRIPT ALL OF THIS AND PASS IN To Workflow
    val partitionCount= nodes * cores * typicalPartitions * factor

    val sc = spark.sparkContext

    import org.apache.log4j._
    val log = LogManager.getRootLogger

    sc.setLogLevel("INFO")

    //Logging Must Be Serializable
    object LogHolder extends Serializable {
      @transient lazy val log = LogManager.getRootLogger// Logger.getLogger(getClass.getName)
    }


    val parquetFileName = "/home/vagrant/tmp/testRDDWaterData.parquet"

    //val parquetSource = spark.read.parquet(parquetFileName)

    //If this is water data we rename
    val parquetSource = spark.read.parquet(parquetFileName).withColumnRenamed("read_value_sum_delta_gallons_double","read_value")

    parquetSource.show()

    parquetSource.describe().show()

    parquetSource.printSchema()

    val configMap:Map[String, String] = spark.conf.getAll

    parquetSource.repartition(partitionCount)

    parquetSource.createOrReplaceTempView("consumption")

    parquetSource.printSchema()
    parquetSource.show()

    val queryRdd =spark.sql("select concat(meter_key,'_',from_unixtime(sample_point, 'MMMM'),'_',from_unixtime(sample_point, 'YYYY')) as window_key,from_unixtime(sample_point) as sample_time,from_unixtime(sample_point, 'YYYY') as year, from_unixtime(sample_point, 'EEEE') as day_of_week,from_unixtime(sample_point, 'MMMM') as month,meter_key, read_value,sample_point,customer_id,fldlat,fldlong from consumption")

    queryRdd.take(10).foreach(println)

    queryRdd.createOrReplaceTempView("consumption_keyed")

    //val queryRddSeptember2015=spark.sql("select meter_key, read_value,sample_point from consumption_keyed where year='2015' and month='September'")
    //val consumptionDF = queryRddSeptember2015.toDF()

    val queryRdd2015=spark.sql("select meter_key, read_value,sample_point from consumption_keyed where year='2015' ")
    val consumptionDF = queryRdd2015.toDF()

    consumptionDF.persist(StorageLevel.MEMORY_ONLY)
    consumptionDF.cache()

    consumptionDF.show()

    sc.setCheckpointDir("/user/hadoop/spark-checkpoint")
    //There may be a warning in Spark 2.0  WARN SparkContext: Spark is not running in local mode,
    // therefore the checkpoint directory must not be on the local filesystem.
    // Directory '/user/hadoop/spark-checkpoint2' appears to be on the local filesystem.
    //It should be safe to ignore this but Specifying hdfs://nn-ip//user/hadoop/spark-checkpoint
    //is best resolution - TODO scrip the nn-ip somehow

    val rowsAsSeqRDD = consumptionDF.rdd.map { x: Row => x.toSeq }

    val pairs = rowsAsSeqRDD.map(x => (x(0), (x(1), x(2))))

    val featureRDDGB = pairs.groupByKey()
    featureRDDGB.persist(StorageLevel.MEMORY_ONLY)
    featureRDDGB.cache()


    //Now calculate all features and make a data frame from them
    val featureRDDAll = featureRDDGB.mapValues { iter =>
      @transient lazy val log = org.apache.log4j.LogManager.getRootLogger
      val timeSeries = new TimeSeries()
      iter.toList.foreach { y =>
        val samplePoint = y._2.asInstanceOf[Integer]
        val readValue = y._1.asInstanceOf[Double]
        timeSeries.addPoint(samplePoint, readValue)
      }
      log.info("DSLOG : "+ timeSeries.readValue().length)
      val featureFactory = new FeatureFactory();

      featureFactory.addAllFeatures();

      val featureMap = featureFactory.getFeatureMap(timeSeries);

      val featureArray = new Array[Double](featureMap.entrySet().size())

      val features = featureMap.entrySet()
      var count = new Integer(0)
      import scala.collection.JavaConversions._
      for (feature <- features) {
        val featureName: String = feature.getKey
        val featureValue: Double = feature.getValue
        featureArray(count)=featureValue
        log.info("DSLOG Feature " + featureName + " = " + featureValue)
        count= count+1
      }
      featureArray
    }

    //featureRDDAll     :    org.apache.spark.rdd.RDD[(Any, Array[Double])]

    val featureRowRDD:RDD[Row] = featureRDDAll.map{
      x =>  Row.fromSeq(Seq(x._1,x._2 ))
    }
    def inputSchema: StructType = StructType(Array( StructField("meter_key",StringType,false), StructField("features",ArrayType(DoubleType,false))))

    //TODO REVISIT
    val featureAllDF = spark.createDataFrame(featureRowRDD,inputSchema)
    featureAllDF.write.parquet("s3://xds-data/FMA/ProcessedParquetFiles/TLQN_Processed/all_features.parquet")


    //fetch metadata data from the catalog
    spark.catalog.listDatabases.show(false)
    spark.catalog.listTables.show(false)

  }

  def runV2(spark:SparkSession):Unit ={

    //Typically we want 2-4 partitions for each CPU in your cluster.
    //Normally, Spark tries to set the number of partitions automatically based on the cluster.
    //m2.4xlarge  8  68.4 -------> n *8 *4 * Factor

    val factor = 4;
    val cores= 16
    val typicalPartitions=4
    val nodes = 6 //TODO SCRIPT ALL OF THIS AND PASS IN To Workflow
    val partitionCount= nodes * cores * typicalPartitions * factor

    val sc = spark.sparkContext

    sc.setLogLevel("WARN")

    val parquetFileName = "/home/vagrant/tmp/testRDDWaterData.parquet"

    //val parquetSource = spark.read.parquet(parquetFileName)

    //If this is water data we rename
    val parquetSource = spark.read.parquet(parquetFileName).withColumnRenamed("read_value_sum_delta_gallons_double","read_value")

    parquetSource.show()

    parquetSource.describe().show()

    parquetSource.printSchema()

    val configMap:Map[String, String] = spark.conf.getAll

    parquetSource.repartition(partitionCount)

    parquetSource.createOrReplaceTempView("consumption")

    parquetSource.printSchema()
    parquetSource.show()

    val queryRdd =spark.sql("select concat(meter_key,'_',from_unixtime(sample_point, 'MMMM'),'_',from_unixtime(sample_point, 'YYYY')) as window_key,from_unixtime(sample_point) as sample_time,from_unixtime(sample_point, 'YYYY') as year, from_unixtime(sample_point, 'EEEE') as day_of_week,from_unixtime(sample_point, 'MMMM') as month,meter_key, read_value,sample_point,customer_id,fldlat,fldlong from consumption")

    queryRdd.take(10).foreach(println)

    queryRdd.createOrReplaceTempView("consumption_keyed")

    //val queryRddSeptember2015=spark.sql("select meter_key, read_value,sample_point from consumption_keyed where year='2015' and month='September'")
    //val consumptionDF = queryRddSeptember2015.toDF()

    val queryRdd2015=spark.sql("select meter_key, read_value,sample_point from consumption_keyed where year='2015' ")
    val consumptionDF = queryRdd2015.toDF()

    consumptionDF.persist(StorageLevel.MEMORY_ONLY)
    consumptionDF.cache()

    consumptionDF.show()

    sc.setCheckpointDir("/user/hadoop/spark-checkpoint")
    //There may be a warning in Spark 2.0  WARN SparkContext: Spark is not running in local mode,
    // therefore the checkpoint directory must not be on the local filesystem.
    // Directory '/user/hadoop/spark-checkpoint2' appears to be on the local filesystem.
    //It should be safe to ignore this but Specifying hdfs://nn-ip//user/hadoop/spark-checkpoint
    //is best resolution - TODO scrip the nn-ip somehow

    val rowsAsSeqRDD = consumptionDF.rdd.map { x: Row => x.toSeq }

    val pairs = rowsAsSeqRDD.map(x => (x(0), (x(1), x(2))))

     /////////////////////////////VARIANCE////////////////////////
    val featureRDDGB = pairs.groupByKey(partitionCount)
    featureRDDGB.persist(StorageLevel.MEMORY_ONLY)
    featureRDDGB.cache()
    featureRDDGB.count()

    val featureRDD = featureRDDGB.mapValues { iter =>
      val timeSeries = new TimeSeries()
      iter.toList.foreach { y =>
        val samplePoint = y._2.asInstanceOf[Integer]
        val readValue = y._1.asInstanceOf[Double]
        timeSeries.addPoint(samplePoint, readValue)
      }
      val varF = new SampleEntropyFeature()
      var featureVal:Double=0.0
      try{
        varF.calculateFeature(timeSeries)
      }
      catch {
        case e:NullPointerException => print("NPE MERGE")

          featureVal=3.14159
      }

      featureVal
    }

    featureRDD.count

    val featureRowRDD:RDD[Row] = featureRDD.map{
      x =>  Row.fromSeq(Seq(x._1,x._2 ))
    }

    import spark.implicits._
    import org.apache.spark.sql.types._

    def inputSchema: StructType = StructType(Array( StructField("meter_key",StringType,true), StructField("variance",DoubleType,true)))

    val feature = spark.createDataFrame(featureRowRDD,inputSchema)

    feature.createOrReplaceTempView("feature")

    feature.show()

    //fetch metadata data from the catalog
    spark.catalog.listDatabases.show(false)
    spark.catalog.listTables.show(false)

  }

  //TODO this implementation is giving a not serializable error
  def runV1(ss: SparkSession) {

    val sc = ss.sparkContext

    sc.setLogLevel("WARN")

    val parquetFileName = "/home/vagrant/tmp/testRDDWaterData.parquet"

    val parquetSource = ss.read.parquet(parquetFileName)

    parquetSource.show()

    val consumptionDF = parquetSource.toDF()

    consumptionDF.printSchema()

    val rowsAsSeqRDD = consumptionDF.rdd.map { x: Row => x.toSeq }

    val pairs = rowsAsSeqRDD.map(x => (x(0), (x(1), x(2))))

    //Now calculate all features and make a data frame from them
    val gbkRDDAllFeatures = pairs.groupByKey.mapValues { iter =>
      val timeSeries = new TimeSeries()
      iter.toList.foreach { y =>
        val samplePoint = y._1.asInstanceOf[Integer]
        val readValue = y._2.asInstanceOf[Double]
        timeSeries.addPoint(samplePoint, readValue)
      }
      val featureFactory = new FeatureFactory();

      featureFactory.addAllFeatures();

      val featureMap = featureFactory.getFeatureMap(timeSeries);

      val levelsFeature: Double = featureMap.get("LevelsFeature")
      val sampleEntropy: Double = featureMap.get("Sample Entropy")
      val thirdQuartile: Double = featureMap.get("75.0Percentile")
      val variance: Double = featureMap.get("Variance")
      val ninetyFifthPercentile: Double = featureMap.get("Ninety Fifth Percentile")
      val sampleCount: Double = featureMap.get("Sample Count")
      val skewness: Double = featureMap.get("Skewness")
      val sumF: Double = featureMap.get("Sum")
      val modeFeature: Double = featureMap.get("ModeFeature")
      val kurtosis: Double = featureMap.get("Kurtosis")
      val standardDeviation: Double = featureMap.get("Standard Deviation")
      val medianFeature: Double = featureMap.get("MedianFeature")
      val fifthPercentile: Double = featureMap.get("Fifth Percentile")
      val maxF: Double = featureMap.get("Max")
      val minF: Double = featureMap.get("Min")
      val meanF: Double = featureMap.get("Mean")
      val approximateEntropy: Double = featureMap.get("Approximate Entropy")

      val featureRecord= FeatureRecord(
        levelsFeature,
        sampleEntropy,
        thirdQuartile,
        variance,
        ninetyFifthPercentile,
        sampleCount,
        skewness,
        sumF,
        modeFeature,
        kurtosis,
        standardDeviation,
        medianFeature,
        fifthPercentile,
        maxF,
        minF,
        meanF,
        approximateEntropy
      )

      featureRecord

      //-------------------------------------------------------------
      //      val featureArray = new Array[Double](featureMap.entrySet().size())
      //
      //      val features = featureMap.entrySet()
      //      var count = new Integer(0)
      //      import scala.collection.JavaConversions._
      //      for (feature <- features) {
      //        val featureName: String = feature.getKey
      //        val featureValue: Double = feature.getValue
      //        featureArray(count)=featureValue
      //        System.out.println("Feature " + featureName + " = " + featureValue)
      //        count= count+1
      //      }
      //      featureArray
      //--------------------------------------------------------------------

    }

    val resultsAll = gbkRDDAllFeatures.count

  }

  def runV0(ss: SparkSession) {
    val sc = ss.sparkContext

    sc.setLogLevel("WARN")

    val parquetFileName = "/home/vagrant/tmp/testRDDWaterData.parquet"

    val parquetSource = ss.read.parquet(parquetFileName)

    parquetSource.show()

    val testDF = parquetSource.toDF()

    testDF.printSchema()

    //testDF.groupBy("meter_key").sum("read_value_sum_delta_gallons_double").collect().foreach(println)

    val rowsAsSeqRDD = testDF.rdd.map { x: Row => x.toSeq }

    val pairs = rowsAsSeqRDD.map(x => (x(0), (x(1), x(2))))

    val gbkRDD = pairs.groupByKey.mapValues { iter =>
      val timeSeries = new TimeSeries()
      iter.toList.foreach { y =>
        val samplePoint = y._1.asInstanceOf[Integer]
        val readValue = y._2.asInstanceOf[Double]
        timeSeries.addPoint(samplePoint, readValue)
      }

      val varF = new VarFeature()

      varF.calculateFeature(timeSeries)
    }

    val results = gbkRDD.collect()
    println(results)

    //Now calculate all features and make a data frame from them
    val gbkRDDAllFeatures = pairs.groupByKey.mapValues { iter =>
      val timeSeries = new TimeSeries()
      iter.toList.foreach { y =>
        val samplePoint = y._1.asInstanceOf[Integer]
        val readValue = y._2.asInstanceOf[Double]
        timeSeries.addPoint(samplePoint, readValue)
      }
      val featureFactory = new FeatureFactory();

      featureFactory.addAllFeatures();

      val featureMap = featureFactory.getFeatureMap(timeSeries);

      val featureArray = new Array[Double](featureMap.entrySet().size())

      val features = featureMap.entrySet()
      var count = new Integer(0)
      import scala.collection.JavaConversions._
      for (feature <- features) {
        val featureName: String = feature.getKey
        val featureValue: Double = feature.getValue
        featureArray(count)=featureValue
        System.out.println("Feature " + featureName + " = " + featureValue)
        count= count+1
      }
      featureArray

    }
    val resultsAll = gbkRDDAllFeatures.collect()
    println(resultsAll)
    val dunno =resultsAll.map{
      x => Row.fromSeq(Seq(x._1,
        x._2(0),x._2(1), x._2(2),  x._2(3), x._2(4),
        x._2(5), x._2(6), x._2(8), x._2(9), x._2(10),
        x._2(11), x._2(12), x._2(13), x._2(14), x._2(15), x._2(16)))
    }

    // Generate the schema based on the string of schema
    val featureString = "LevelsFeature SampleEntropy ThirdQuartile Variance NinetyFifthPercentile SampleCount Skewness Sum ModeFeature Kurtosis StandardDeviation MedianFeature FifthPercentile Max Min Mean ApproximateEntropy"

    val featureFields = featureString.split(" ")
      .map(fieldName => StructField(fieldName, DoubleType, nullable = true))
    val keyField = StructField("meter_key", DoubleType, nullable = true)
    val schema = StructType(featureFields)

//    val featureDF = ss.createDataFrame(dunno,schema)
//
//    println(newRDD.getClass)

  }

  def testRDDManipulations(ss: SparkSession) {
    val sc = ss.sparkContext

    case class Record(meter_key: String, read_value: Double, sample_point: Int)

    def recordToRow(rec : Record) : Row ={
      Row.fromSeq(Seq(rec.meter_key,rec.read_value,rec.sample_point))
    }

    def seqToRow(seq : Seq[String]) : Row ={
      Row.fromSeq(Seq(seq(1),seq(2),seq(3) ))
    }

    val recs = Array(Record("COBB_1N6027670038_45930061", 1000.0, 1),
      Record("COBB_1N6027670038_45930061", 2000.0, 3),
      Record("COBB_1N6027670038_45930061", 3000.0, 5),
      Record("COBB_1N6027670038_45930061", 2000.0, 2),
      Record("COBB_1N6027670038_45930061", 4000.0, 5),
      Record("COBB_1N6027670038_45930061", 6000.0, 4),
      Record("BOBB_1N6027670038_45930061", 1000.0, 2),
      Record("BOBB_1N6027670038_45930061", 2000.0, 3),
      Record("BOBB_1N6027670038_45930061", 2000.0, 1),
      Record("BOBB_1N6027670038_45930061", 6000.0, 5),
      Record("BOBB_1N6027670038_45930061", 6000.0, 4)
    )

    val recordRDD: RDD[Record] = sc.parallelize(recs)
    import ss.implicits._

    println(recordRDD.count)

    //TODO This should work
//    val recordDS:Dataset[Record] =  ss.createDataset( recordRDD)
//
//
//    val recsSEQ = Array(Seq("COBB_1N6027670038_45930061", "1000.0", "1"),
//      Seq("COBB_1N6027670038_45930061", "2000.0", "3"),
//      Seq("COBB_1N6027670038_45930061", "3000.0", "5")
//    )
//
//    val seqRDD:RDD[Seq[String]] = sc.parallelize(recsSEQ)
//
//    println(seqRDD.toDS().count())
//
//    println(seqRDD.getClass)
//
//    val rowRDD:RDD[Row] = seqRDD.map{
//      seq =>  Row.fromSeq(Seq(seq(1),seq(2),seq(3) ))
//    }
//
//    println(rowRDD.getClass)
//
//    // The schema is encoded in a string
//    val schemaString = "meter_key read_value sample_point"
//
//    // Generate the schema based on the string of schema
//    val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

  }
}
