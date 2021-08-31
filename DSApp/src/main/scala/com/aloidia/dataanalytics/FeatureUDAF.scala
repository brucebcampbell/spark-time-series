package com.aloidia.datascience

import com.aloidia.datascience.univariatefeatures.VarFeature
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import scala.collection.JavaConverters._
import org.apache.spark.SparkException
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.expressions.UnsafeRow
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StructType }

//TODO This is not working, and it's not implemented correctly.
class FeatureUDAF() extends UserDefinedAggregateFunction {

  def inputSchema: StructType = StructType(Array( StructField("sample_point",IntegerType,true), StructField("read_value",DoubleType,true)))

  // Intermediate Schema
  def bufferSchema = StructType(Array(  StructField("sample_point",IntegerType,true), StructField("read_value",DoubleType,true)))

  // Returned Data Type .
  def dataType: DataType = DoubleType

  val timeSeries = new TimeSeries()

  // Self-explaining
  def deterministic = true

  // This function is called whenever key changes
  def initialize(buffer: MutableAggregationBuffer) = {
    buffer(0) = 0.toInt
    buffer(1) = 0.toDouble

  }

  // Iterate over each entry of a group
  def update(buffer: MutableAggregationBuffer, input: Row) = {
    try {
      buffer(0) = buffer.getInt(0) + input.getInt(0)

      buffer(1) = buffer.getDouble(1) + input.getDouble(1)

      //Completely Wrong - but Spark does not want up to extend MutableAggregationBuffer
      println("Adding buffer "  + buffer.getInt(0) + " " + buffer.getDouble(1) )

      timeSeries.addPoint( buffer.getInt(0),buffer.getDouble(1))

      println("Adding input "  + input.getInt(0) + " " + input.getDouble(1))
      timeSeries.addPoint(input.getInt(0),input.getDouble(1))

      println("TS LENGTH = " + timeSeries.readValue().length)
    }
    catch {
      case e:NullPointerException => print("NPE UPDATE")
    }

  }

  // Merge two partial aggregates
  def merge(buffer1: MutableAggregationBuffer, buffer2: Row) = {

    try {
      buffer1(0) = buffer1.getInt(0) + buffer2.getInt(0)

      buffer1(1) = buffer1.getDouble(1) + buffer2.getDouble(1)

      //TODO NO IDEA Completely Wrong - but Spark does not want up to extend MutableAggregationBuffer

      println("Adding buffer1 "  + buffer1.getInt(0) + " " + buffer1.getDouble(1))
      timeSeries.addPoint( buffer1.getInt(0),buffer1.getDouble(1))

      println("Adding buffer2 "  + buffer2.getInt(0) + " " + buffer2.getDouble(1))
      timeSeries.addPoint(buffer2.getInt(0),buffer2.getDouble(1))

      println("TS LENGTH = " + timeSeries.readValue().length)
    }
    catch {
      case e:NullPointerException => print("NPE MERGE")
    }

  }

  // Called after all the entries are exhausted.
  def evaluate(buffer: Row) = {
    val read_value = buffer.getDouble(1)
    val sample_point = buffer.getInt(0)

    val bufferLength = buffer.length

    println("EVALUATE TS LENGTH = " + timeSeries.readValue().length)

    val varF = new VarFeature()

    val variance = varF.calculateFeature(timeSeries)

    variance
  }

}

