import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkFiles
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql._

val conf = new SparkConf()
  .setAppName("The swankiest Spark app ever")
  .setMaster("local[*]")

val sc = new SparkContext(conf)
//val sqlContext = new SQLContext(sc)
//Read Test
//val parquetSource = sqlContext.read.parquet("/home/vagrant/tmp/testRDDWaterData.parquet")
//parquetSource.show()
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
val rs = sc.parallelize(recs)
rs.collect().foreach(println)
// Generate pair RDD neccesary to call groupByKey and group it
val key: RDD[((Double, Int), Iterable[Record])] = rs.keyBy(r => (r.read_value, r.sample_point)).groupByKey
// Once grouped you need to sort values of each Key
val values: RDD[((Double, Int), List[Record])] = key.mapValues(iter => iter.toList.sortBy(_.sample_point).reverse)
// Print result
values.collect.foreach(println)
