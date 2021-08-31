package com.aloidia.datascience

import com.aloidia.datascience.multivariatefeatures
import com.aloidia.datascience.multivariatefeatures.ACFFeature
import com.aloidia.datascience.univariatefeatures.FeatureFactory
import org.apache.log4j

//import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.mllib.clustering.KMeans

import com.aloidia.datascience.TimeSeriesPlot

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.apache.spark.storage.StorageLevel
import scala.collection.JavaConverters._
import org.apache.spark.SparkException
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.expressions.UnsafeRow
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StructType }

class ACFWorkflow {

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

    val parquetFileName = "/home/vagrant/tmp/test_reads.parquet"

    //val parquetSource = spark.read.parquet(parquetFileName)

    //If this is water data we rename
    val parquetSource = spark.read.parquet(parquetFileName)

    parquetSource.show()

    parquetSource.take(1).foreach(println)

    parquetSource.describe().show()

    parquetSource.printSchema()

    val configMap:Map[String, String] = spark.conf.getAll

    parquetSource.repartition(partitionCount)

    parquetSource.createOrReplaceTempView("consumption")

    parquetSource.printSchema()
    parquetSource.show()

    val consumptionDF = parquetSource.toDF()

    //DEBUG
    //val consumptionDF = spark.sql("select * from consumption where meter_key= 'NVE_CC031535494_49404194' ")

    consumptionDF.persist(StorageLevel.MEMORY_ONLY)
    consumptionDF.cache()

    consumptionDF.show()

    //sc.setCheckpointDir("/user/hadoop/spark-checkpoint")
    //There may be a warning in Spark 2.0  WARN SparkContext: Spark is not running in local mode,
    // therefore the checkpoint directory must not be on the local filesystem.
    // Directory '/user/hadoop/spark-checkpoint2' appears to be on the local filesystem.
    //It should be safe to ignore this but Specifying hdfs://nn-ip//user/hadoop/spark-checkpoint
    //is best resolution - TODO scrip the nn-ip somehow

    val rowsAsSeqRDD = consumptionDF.rdd.map { x: Row => x.toSeq }

    //DEBUG ONLY
    //rowsAsSeqRDD.collect().foreach(println)

    //read_value is int in the SDA DataLake
    val pairs = rowsAsSeqRDD.map(x => (x(0), (x(1), x(2), x(0) )))

    val featureRDDGB = pairs.groupByKey()
    featureRDDGB.persist(StorageLevel.MEMORY_ONLY)
    featureRDDGB.cache()

    val meterCount = featureRDDGB.count()

    val featureRDD = featureRDDGB.mapValues { iter =>

      val timeSeries = new TimeSeries()


      iter.toList.foreach { y =>
        val samplePoint = y._2.asInstanceOf[Integer]
        val readValue = y._1.asInstanceOf[Integer].toDouble

        //Revisit - setting key every time!
        val key = y._3.toString
        timeSeries.setMeterKey(key)

        timeSeries.addPoint(samplePoint, readValue)
      }

      val lag:Int = 24*4//672
      var featureVal:Array[java.lang.Double] = new Array[java.lang.Double](lag);
      val featureF = new ACFFeature()
      featureF.setLag(lag)

      try{
        //LogHolder.log.error("DS LOG : "+ timeSeries.readValue().length)
        featureVal =  featureF.calculateFeature(timeSeries)

        val fileNameBase = "/home/vagrant/tmp/img" + timeSeries.getMeterKey
        val tsp = new TimeSeriesPlot(timeSeries,fileNameBase+ ".png",timeSeries.getMeterKey,1400,1400)
        tsp.render()

        val acfts = new TimeSeries()

        for( i <- 0 to featureVal.length-1 )
        {
          acfts.addPoint(i,featureVal(i))
        }
        val acffileNameBase = "/home/vagrant/tmp/img" + timeSeries.getMeterKey
        val acftsp = new TimeSeriesPlot(acfts,fileNameBase+ "acf.png",timeSeries.getMeterKey,1400,1400)
        acftsp.render()
        //val featureVal = featureF.getFeatureValue()

      }
      catch {
        case e:NullPointerException => print("NPE MERGE")
      }

//      for( i <- 0 to lag-1)
//      {
//        print(featureVal(i)   )
//      }
//      print("\n")

      featureVal
    }

    //println(featureRDD.count())

    val sampleFrac = 20 / meterCount

    val smallRDD= featureRDD.sample(false,sampleFrac,0)

    smallRDD.cache()

    //println(smallRDD.count())

    smallRDD.collect()

    //println(smallRDD.count())

//    val t2= featureRDD.map{r =>
//      val array = r._2.toSeq.toArray
//      array.map(_.toDouble)
//    }
//
//    val vectors = t2.map(x => Vectors.dense(x))
//
//    println(vectors.count() )
//
//    val vectorsCleaned = vectors.filter{ v =>
//      println(v(0))
//      val result = ! v(0).equals( Double.NaN)
//      result }
//
//    println(vectorsCleaned.count() )
//
//    val dunno = vectorsCleaned.map{
//      v =>
//        println("--------------" + v(0))
//    }
//
//    vectorsCleaned.take(6).foreach(println)
//
//    // Cluster the data into classes using KMeans
//    val numIterations = 50
//    val wssArray:Array[Double]= new Array(20)
//    var i=0
//    for(i <- 5 to 15)
//    {
//      val clusters = KMeans.train(vectorsCleaned, i, numIterations)
//
//      // Evaluate clustering by computing Within Set Sum of Squared Errors
//      val WSSSE = clusters.computeCost(vectors)
//      wssArray(i)=WSSSE
//
//      println("Within Set Sum of Squared Errors = " + WSSSE)
//    }
//
//    //fetch metadata data from the catalog
//    spark.catalog.listDatabases.show(false)
//    spark.catalog.listTables.show(false)
//
  }

}
