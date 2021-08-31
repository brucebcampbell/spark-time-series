package com.aloidia.datascience

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, Dataset, SparkSession}
import org.apache.spark.sql.SparkSession.Builder

object App {

  def main(args : Array[String]) {

    val sparkSession = SparkSession.builder
      .appName("The swankiest Spark app ever")
      .master("local[*]")
      .getOrCreate()

    val sc = sparkSession.sparkContext

    val col = sc.parallelize(0 to 100 by 5)
    val smp = col.sample(true, 4)
    val colCount = col.count
    val smpCount = smp.count

    println("orig count = " + colCount)
    println("sampled count = " + smpCount)

    val be = new BreezeExample()
    be.run()

    val acfApp = new ACFWorkflow()

    acfApp.runV3(sparkSession)

    //We're having a problem with the implicits in the dev env.
    //simpleTest(sparkSession)

    //val fw = new FeatureWorkflow()

    //fw.runV2(sparkSession)

    //fw.runV1(sparkSession)

    //fw.testRDDManipulations(sparkSession)

    //fw.runV0(sparkSession)
  }

  def simpleTest(sparkSession : SparkSession) :Unit =
  {
    val spark = sparkSession

    val sc = spark.sparkContext

    case class Record(meter_key: String, read_value: Double, sample_point: Int)
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

    def inputSchema: StructType = StructType(Array( StructField("meter_key",StringType,true), StructField("sample_point",IntegerType,true), StructField("read_value",DoubleType,true)))

    val recordRDD: RDD[Record] = sc.parallelize(recs)

    import spark.implicits._

    //Much sadness - we were not able to get implicits working in the development environment.
    //This works on the cluster
    //val recordDS:Dataset[Record] =  spark.createDataset( recordRDD)

//    recordDS.printSchema
//
//    val rowRDD:RDD[Row] = recordRDD.map{
//      rec => Row.fromSeq(Seq(rec.meter_key,rec.read_value,rec.sample_point))
//    }
//
//    val testDF =spark.createDataFrame(rowRDD,inputSchema)

  }

}
