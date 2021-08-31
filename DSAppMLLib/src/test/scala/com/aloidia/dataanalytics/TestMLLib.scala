package com.aloidia.datascience

import java.io.File

import com.aloidia.datascience.TimeSeries
import com.aloidia.datascience.univariatefeatures.FeatureFactory
import org.junit._
import Assert._
import org.apache.spark.mllib.linalg.{Vector, Vectors, Matrix, SingularValueDecomposition}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.rdd.RDD

//SVD

//Linear Regression
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.regression.LabeledPoint

//Kmeans
import org.apache.spark.mllib.clustering.KMeans

//SVM
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint

//For L1 regularized SVM and Logistic Regression
import org.apache.spark.mllib.optimization.L1Updater

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

@Test
class TestMLLib {
  val sc: SparkContext= new SparkContext(new SparkConf().setAppName("aloidia-ML").setMaster("local[2]").set("spark.executor.memory", "1g"))


  @Test
  def RunLinearRegression() {

//    var sc: SparkContext= new SparkContext(new SparkConf().setAppName("aloidia-ML").setMaster("local[2]").set("spark.executor.memory", "1g"))

    val file: File = new File(getClass.getResource("lpsa.data").getFile)
    val filename: String = file.getAbsolutePath

    val data = sc.textFile(filename)


    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }

    // Building the model
    val numIterations = 100
    val model = LinearRegressionWithSGD.train(parsedData, numIterations)

    // Evaluate model on training examples and compute training error
    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val MSE = valuesAndPreds.map { case (v, p) => math.pow((v - p), 2) }.mean()
    println("training Mean Squared Error = " + MSE)
  }

  @Test
  def RunSVM() {

//    var sc: SparkContext= new SparkContext(new SparkConf().setAppName("aloidia-ML").setMaster("local[2]").set("spark.executor.memory", "1g"))

    val file: File = new File(getClass.getResource("libsvm_data.data").getFile)
    val filename: String = file.getAbsolutePath

    // Load training data in LIBSVM format.
    val data = MLUtils.loadLibSVMFile(sc, filename)

    // Split data into training (60%) and test (40%).
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    // Run training algorithm to build the model
    val numIterations = 100
    val model = SVMWithSGD.train(training, numIterations)

    // Clear the default threshold.
    model.clearThreshold()

    // Compute raw scores on the test set.
    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }

    // Get evaluation metrics.
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    println("Area under ROC = " + auROC)
  }

  @Test
  def RunL1SVM() {

//    var sc: SparkContext= new SparkContext(new SparkConf().setAppName("aloidia-ML").setMaster("local[2]").set("spark.executor.memory", "1g"))

    val file: File = new File(getClass.getResource("libsvm_data.data").getFile)
    val filename: String = file.getAbsolutePath


    // Load training data in LIBSVM format.
    val data = MLUtils.loadLibSVMFile(sc, filename)

    // Split data into training (60%) and test (40%).
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    // Run training algorithm to build the model
    val numIterations = 100

    val svmAlg = new SVMWithSGD()
    svmAlg.optimizer.setNumIterations(200).setRegParam(0.1).
      setUpdater(new L1Updater)
    val modelL1 = svmAlg.run(training)

    // Compute raw scores on the test set.
    val scoreAndLabels = test.map { point =>
      val score = modelL1.predict(point.features)
      (score, point.label)
    }

    // Get evaluation metrics.
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    println("Area under ROC = " + auROC)
  }

  @Test
  def RunKMeans() {

//    var sc: SparkContext= new SparkContext(new SparkConf().setAppName("aloidia-ML").setMaster("local[2]").set("spark.executor.memory", "1g"))

    val file: File = new File(getClass.getResource("kmeans_data.data").getFile)
    val filename: String = file.getAbsolutePath

    val data = sc.textFile(filename)

    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble)))

    // Cluster the data into two classes using KMeans
    val numClusters = 2
    val numIterations = 20
    val clusters = KMeans.train(parsedData, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)
    println("Within Set Sum of Squared Errors = " + WSSSE)
  }

  def RunSVD() {
//    var sc: SparkContext= new SparkContext(new SparkConf().setAppName("aloidia-ML").setMaster("local[2]").set("spark.executor.memory", "1g"))

    val file: File = new File(getClass.getResource("SVD_data.txt").getFile)
    val filename: String = file.getAbsolutePath

    // Load and parse the data
    val data = sc.textFile(filename)

    val parsedData = data.map(x => Vectors.dense(x.split(',').map(_.toDouble))).cache()

    val rows = parsedData

    val mat: RowMatrix = new RowMatrix(rows)

    // Compute the top 20 singular values and corresponding singular vectors.
    val svd: SingularValueDecomposition[RowMatrix, Matrix] = mat.computeSVD(20, computeU = true)
    val U: RowMatrix = svd.U // The U factor is a RowMatrix.
    val s: Vector = svd.s // The singular values are stored in a local dense vector.
    val V: Matrix = svd.V // The V factor is a local dense matrix.
  }

  @Test
  def RunPCA() {
//    var sc: SparkContext= new SparkContext(new SparkConf().setAppName("aloidia-ML").setMaster("local[2]").set("spark.executor.memory", "1g"))

    val file: File = new File(getClass.getResource("PCA_data.data").getFile)
    val filename: String = file.getAbsolutePath

    // Load and parse the data
    val data = sc.textFile(filename)

    val parsedData = data.map(x => Vectors.dense(x.split(',').map(_.toDouble))).cache()

    //val rows = parsedData

    //val mat: RowMatrix = new RowMatrix(rows)

    // Compute the top 10 principal components.
    //val pc: Matrix = mat.computePrincipalComponents(10) // Principal components are stored in a local dense matrix.

    // Project the rows to the linear space spanned by the top 10 principal components.
    //val projected: RowMatrix = mat.multiply(pc)

  }

}


