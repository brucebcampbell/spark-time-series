package com.aloidia.datascience
 
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import scala.collection.JavaConverters._
import org.apache.spark.SparkException
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.expressions.UnsafeRow
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.functions._

// User-defined Aggregate Function Interface
// For power users, Spark 1.5 introduces an experimental API for user-defined aggregate functions (UDAFs). 
// These UDAFs can be used to compute custom calculations over groups of input data (in contrast, 
// UDFs compute a value looking at a single input row), such as calculating geometric mean or calculating 
// the product of values for every group.

// A UDAF maintains an aggregation buffer to store intermediate results for every group of input data. 
// It updates this buffer for every input row. Once it has processed all input rows, it generates a result 
// value based on values of the aggregation buffer.

// An UDAF inherits the base class UserDefinedAggregateFunction and implements the following eight methods, which are:

// inputSchema: inputSchema returns a StructType and every field of this StructType represents an input argument of this UDAF.
// bufferSchema: bufferSchema returns a StructType and every field of this StructType 
//                 represents a field of this UDAF’s intermediate results.
// dataType: dataType returns a DataType representing the data type of this UDAF’s returned value.
// deterministic: deterministic returns a boolean indicating if this UDAF always generate the same result
//             for a given set of input values.
// initialize: initialize is used to initialize values of an aggregation buffer, represented by a MutableAggregationBuffer.
// update: update is used to update an aggregation buffer represented by a MutableAggregationBuffer for an input Row.
// merge: merge is used to merge two aggregation buffers and store the result to a MutableAggregationBuffer.
// evaluate: evaluate is used to generate the final result value of this UDAF based on values stored in an 
//           aggregation buffer represented by a Row.

class LongProductSum extends UserDefinedAggregateFunction {
  def inputSchema: StructType = new StructType()
    .add("a", LongType)
    .add("b", LongType)

  def bufferSchema: StructType = new StructType()
    .add("product", LongType)

  def dataType: DataType = LongType

  def deterministic: Boolean = true

  def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
  }

  def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (!(input.isNullAt(0) || input.isNullAt(1))) {
      buffer(0) = buffer.getLong(0) + input.getLong(0) * input.getLong(1)
    }
  }

  def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
  }

  def evaluate(buffer: Row): Any =
    buffer.getLong(0)
}


 
import org.apache.spark.sql.expressions.MutableAggregationBuffer
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
 
class GeometricMean extends UserDefinedAggregateFunction {
  def inputSchema: org.apache.spark.sql.types.StructType =
    StructType(StructField("value", DoubleType) :: Nil)
 
  def bufferSchema: StructType = StructType(
    StructField("count", LongType) ::
    StructField("product", DoubleType) :: Nil
  )
 
  def dataType: DataType = DoubleType
 
  def deterministic: Boolean = true
 
  def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
    buffer(1) = 1.0
  }
 
  def update(buffer: MutableAggregationBuffer,input: Row): Unit = {
    buffer(0) = buffer.getAs[Long](0) + 1
    buffer(1) = buffer.getAs[Double](1) * input.getAs[Double](0)
  }
 
  def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getAs[Long](0) + buffer2.getAs[Long](0)
    buffer1(1) = buffer1.getAs[Double](1) * buffer2.getAs[Double](1)
  }
 
  def evaluate(buffer: Row): Any = {
    math.pow(buffer.getDouble(1), 1.toDouble / buffer.getLong(0))
  }
}

 
//Extend UserDefinedAggregateFunction to write custom aggregate function
//You can also specify any constructor arguments. For instance you 
//can have CustomMean(arg1: Int, arg2: String)
class CustomMean() extends UserDefinedAggregateFunction {
 
  // Input Data Type Schema
  def inputSchema: StructType = StructType(Array(StructField("item", DoubleType)))
 
  // Intermediate Schema
  def bufferSchema = StructType(Array(
    StructField("sum", DoubleType),
    StructField("cnt", LongType)
  ))
 
  // Returned Data Type .
  def dataType: DataType = DoubleType
 
  // Self-explaining
  def deterministic = true
 
  // This function is called whenever key changes
  def initialize(buffer: MutableAggregationBuffer) = {
    buffer(0) = 0.toDouble // set sum to zero
    buffer(1) = 0L // set number of items to 0
  }
 
  // Iterate over each entry of a group
  def update(buffer: MutableAggregationBuffer, input: Row) = {
    buffer(0) = buffer.getDouble(0) + input.getDouble(0)
    buffer(1) = buffer.getLong(1) + 1
  }
 
  // Merge two partial aggregates
  def merge(buffer1: MutableAggregationBuffer, buffer2: Row) = {
    buffer1(0) = buffer1.getDouble(0) + buffer2.getDouble(0)
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }
 
  // Called after all the entries are exhausted.
  def evaluate(buffer: Row) = {
    buffer.getDouble(0)/buffer.getLong(1).toDouble
  }
 
}