package com.aloidia.datascience

import com.aloidia.datascience.TimeSeries
import com.aloidia.datascience.univariatefeatures.FeatureFactory
import org.apache.spark.sql.{SQLContext, SparkSession, Column, Row}
import org.apache.spark.sql.types._
import org.junit._
import Assert._

/*
TODO REVISIT The UDAF and the test are not working
 */


@Test
class TestUDAF {


  @Test
  def testFeatureCalculateFromScala(): Unit = {

    val sparkSession = SparkSession.builder
      .appName("The swankiest Spark app ever")
      .master("local[*]")
      .getOrCreate()

    val sc = sparkSession.sparkContext

    case class Record(meter_key:String, read_value:Double , sample_point:Int)
    val recs = Array (Record("COBB_1N6027670038_45930061",1000.0,1),
      Record("COBB_1N6027670038_45930061",2000.0,3),
      Record("COBB_1N6027670038_45930061",3000.0,5),
      Record("COBB_1N6027670038_45930061",2000.0,2),
      Record("COBB_1N6027670038_45930061",4000.0,5),
      Record("COBB_1N6027670038_45930061",6000.0,4),
      Record( "BOBB_1N6027670038_45930061",1000.0,2),
      Record("BOBB_1N6027670038_45930061",2000.0,3),
      Record("BOBB_1N6027670038_45930061",2000.0,1),
      Record("BOBB_1N6027670038_45930061",6000.0,5),
      Record("BOBB_1N6027670038_45930061",6000.0,4)
    )

    val recordRDD = sc.parallelize(recs)


    println(recordRDD.getClass)

    // The schema is encoded in a string
    val schemaString = "meter_key read_value sample_point"

    // Generate the schema based on the string of schema
    val schema = StructType(  schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

    def inputSchema: StructType = StructType(Array( StructField("meter_key",StringType,true), StructField("sample_point",IntegerType,true), StructField("read_value",DoubleType,true)))

    val sqlContext = new SQLContext(sc)

    //VERY IMPORTANT we need to create an instance of org.apache.spark.sql.SQLContext before being able to use its members and methods
    import sqlContext.implicits._

    recordRDD.take(1).foreach(println)

    val testDFSeq = recordRDD.map(x  => Seq(x.meter_key,x.read_value,x.sample_point))

    println(testDFSeq.getClass)

//    val testDF = recordRDD.toDF()
//
//    //This should work
//    //val testDF = sqlContext.createDataFrame(recordRDD,inputSchema)
//
//    val keyCol = new Column("meter_key")
//    val samplePointCol = new Column("sample_point")
//    val readCol = new Column("read_value_sum_dlta_gallons_double")
//    val fm = new FeatureUDAF
//
//    sqlContext.udf.register("fm", fm)
//    val varianceFeature = testDF.groupBy(keyCol).agg(fm(samplePointCol, readCol).as("Feature"))
//    varianceFeature.show()

    println(42)
  }


}
