package com.aloidia.datascience

object TimeSeriesDriver {
  def main(args: Array[String]) {
    println("Time Series Analysis")

    {
      val factorsDir = "factors/"
      val instrumentsDir = "instruments/"
      val numTrials = 10000.toString
      val parallelism = 10.toString
      val horizon = 1.toString
      val parametersHVAR: Array[String] = Array(factorsDir, instrumentsDir, numTrials, parallelism, horizon)

      val garchDrive = new GARCHDriver();

      garchDrive.Run_GARCH_log_likelihood()

      garchDrive.Run_fit_model()

      garchDrive.Run_fit_model2()

      garchDrive.Run_gradient()

      garchDrive.Run_standardize_filter()
    }
  }
}
