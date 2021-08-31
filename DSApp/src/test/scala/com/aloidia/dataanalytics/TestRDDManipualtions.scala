package com.aloidia.datascience

import org.apache.spark.sql.{SQLContext, SparkSession, Column, Row}
import org.junit._
import Assert._

@Test
class TestRDDManipualtions {

  /*
  We can use toDF() to  convert
    RDD[Int]
    RDD[Long]
    RDD[String]
    RDD[T <: scala.Product]
 toDF  can be imported by import sqlContext.implicits_.

 This approach only works for the types of RDDs listed above

 Here we explore and learn how to convert generic RDD's to and from DataFrames
   */

  @Test
  def rowRDD: Unit = {

    case class FeatureRecord(meter_key: String,
                             LevelsFeature: Double,
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
                             ApproximateEntropy: Double)

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
//
//    val recordRDD = sc.parallelize(recs)
//
//
//    println(recordRDD.getClass)
//
//    // The schema is encoded in a string
//    val schemaString = "meter_key read_value sample_point"
//
//    // Generate the schema based on the string of schema
//    val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))


  }
}
