setwd('/home/vagrant/spark-time-series/TSFeatures/test')

numSamples <-1000

norm <- rnorm(numSamples)
tics <- 1:numSamples
df <- cbind(tics,norm)
write.csv(df,'normal.csv',row.names=FALSE)

lambda <- 10
poisson <- rpois(numSamples, lambda)
df <- cbind(tics,poisson)
write.csv(df,'poisson_lambda_10.csv',row.names=FALSE)

uniform <- rep(1,numSamples)
df <- cbind(tics,uniform)
write.csv(df,'constant.csv',row.names=FALSE)

unif <- runif(numSamples)
tics <- 1:numSamples
df <- cbind(tics,unif)
write.csv(df,'uniform.csv',row.names=FALSE)


