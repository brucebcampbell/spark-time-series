package com.aloidia.datascience

import breeze.linalg._
class BreezeExample {
  def run()
  {
    val x = DenseVector.zeros[Double](5)

    x.foreach(println)

  }

}
