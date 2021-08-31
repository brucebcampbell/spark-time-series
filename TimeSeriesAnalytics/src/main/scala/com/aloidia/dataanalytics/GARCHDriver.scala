package com.aloidia.datascience


import breeze.linalg.DenseVector

import com.cloudera.sparkts.GARCH
import com.cloudera.sparkts.GARCHModel

import com.cloudera.sparkts.ARGARCHModel
import com.cloudera.sparkts.ARGARCH


import org.apache.commons.math3.random.MersenneTwister

class GARCHDriver {
  def Run_GARCH_log_likelihood() {
    val model = new GARCHModel(.2, .3, .4)
    val rand = new MersenneTwister(5L)
    val n  = 10000

    val ts = new DenseVector(model.sample(n, rand))
    val logLikelihoodWithRightModel = model.logLikelihood(ts)

    val logLikelihoodWithWrongModel1 = new GARCHModel(.3, .4, .5).logLikelihood(ts)
    val logLikelihoodWithWrongModel2 = new GARCHModel(.25, .35, .45).logLikelihood(ts)
    val logLikelihoodWithWrongModel3 = new GARCHModel(.1, .2, .3).logLikelihood(ts)

    assert(logLikelihoodWithRightModel > logLikelihoodWithWrongModel1)
    assert(logLikelihoodWithRightModel > logLikelihoodWithWrongModel2)
    assert(logLikelihoodWithRightModel > logLikelihoodWithWrongModel3)
    assert(logLikelihoodWithWrongModel2 > logLikelihoodWithWrongModel1)
  }

  def Run_gradient() {
    val alpha = 0.3
    val beta = 0.4
    val omega = 0.2
    val genModel = new GARCHModel(omega, alpha, beta)
    val rand = new MersenneTwister(5L)
    val n = 10000

    val ts = new DenseVector(genModel.sample(n, rand))

    val model=new GARCHModel(omega + .1, alpha + .05, beta + .1)
    val likelihood = model.logLikelihood(ts)
    println(likelihood)
 /*   val gradient1 = new GARCHModel(omega + .1, alpha + .05, beta + .1).gradient(ts)
    assert(gradient1.forall(_ < 0.0))
    val gradient2 = new GARCHModel(omega - .1, alpha - .05, beta - .1).gradient(ts)
    assert(gradient2.forall(_ > 0.0))*/
  }

  def Run_fit_model() {
    val omega = 0.2
    val alpha = 0.3
    val beta = 0.5
    val genModel = new ARGARCHModel(0.0, 0.0, alpha, beta, omega)
    val rand = new MersenneTwister(5L)
    val n = 10000

    val ts = new DenseVector(genModel.sample(n, rand))

    val model = GARCH.fitModel(ts)
    assert(model.omega - omega < .1) // TODO: we should be able to be more accurate
    assert(model.alpha - alpha < .02)
    assert(model.beta - beta < .02)
  }

  def Run_fit_model2() {
    val ts = DenseVector(0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,
      0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,
      -0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,
      -0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,
      -0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,
      0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,
      0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,
      0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,
      -0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,
      0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,
      -0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,
      -0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,
      -0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,
      0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,
      0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,
      -0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,
      -0.1,0.1,0.0,-0.01,0.00,-0.1,0.1,-0.2,-0.1,0.1,0.0,-0.01,0.00,-0.1)
    val model = ARGARCH.fitModel(ts)
    println(s"alpha: ${model.alpha}")
    println(s"beta: ${model.beta}")
    println(s"omega: ${model.omega}")
    println(s"c: ${model.c}")
    println(s"phi: ${model.phi}")
  }

  def Run_standardize_filter() {
    val model = new ARGARCHModel(40.0, .4, .2, .3, .4)
    val rand = new MersenneTwister(5L)
    val n  = 10000

    val ts = new DenseVector(model.sample(n, rand))

    // de-heteroskedasticize
    val standardized = model.removeTimeDependentEffects(ts, DenseVector.zeros[Double](n))
    // heteroskedasticize
    val filtered = model.addTimeDependentEffects(standardized, DenseVector.zeros[Double](n))

    assert((filtered - ts).toArray.forall(math.abs(_) < .001))
  }
}

