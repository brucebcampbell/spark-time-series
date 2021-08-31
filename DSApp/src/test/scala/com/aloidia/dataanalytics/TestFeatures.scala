package com.aloidia.datascience

import com.aloidia.datascience.TimeSeries
import com.aloidia.datascience.univariatefeatures.FeatureFactory
import org.junit._
import Assert._

@Test
class TestFeatures {


    @Test
    def testFeatureCalculateFromScala(  ): Unit = {
      val timeSeries = new TimeSeries()
      timeSeries.addPoint(1, 10.0)
      timeSeries.addPoint(2, 20.0)
      timeSeries.addPoint(3, 20.0)
      timeSeries.addPoint(4, 10.0)
      timeSeries.addPoint(5, 30.0)
      timeSeries.toString()

      import com.aloidia.datascience.univariatefeatures._
      val varF = new VarFeature();
      val variance = varF.calculateFeature(timeSeries)

      assert(variance==70)
    }

  @Test
  def testFeatureHarnessFromScala() : Unit = {
    val timeSeries = new TimeSeries()
    timeSeries.addPoint(1, 10.0)
    timeSeries.addPoint(2, 20.0)
    timeSeries.addPoint(3, 20.0)
    timeSeries.addPoint(4, 10.0)
    timeSeries.addPoint(5, 30.0)
    timeSeries.addPoint(6, 30.0)
    timeSeries.addPoint(7, 30.0)
    timeSeries.addPoint(8, 30.0)
    timeSeries.addPoint(9, 30.0)
    timeSeries.addPoint(10, 30.0)
    timeSeries.addPoint(11, 30.0)
    timeSeries.toString()

    val featureFactory = new FeatureFactory();

    featureFactory.addAllFeatures();

    val featureMap = featureFactory.getFeatureMap(timeSeries);

    val featureString =featureMap.keySet();

    val features = featureMap.entrySet()
    import scala.collection.JavaConversions._
    for (feature <- features) {
      val featureName: String = feature.getKey
      val featureValue: Double = feature.getValue
      System.out.println("Feature " + featureName + " = " + featureValue)
    }

    assert(featureMap.get("Sum")==270)


  }


}


