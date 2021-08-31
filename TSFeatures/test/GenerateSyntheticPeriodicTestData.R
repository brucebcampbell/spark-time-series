setwd('~/timeSeriesEngine/results')

library("plyr")
library("moments")
library("e1071")
library("pracma")

wdw <- 7*24 # one week of data chunk for example
increment <- wdw - 24 # one day of overlapping, increase the window every 6 days
num_component <- 10

xr <- (1:1000)*.01
x1=1/10*sin(pi*xr)
x2=1/2*sin(2*pi*(xr+.2))
x3=1/3*sin(3*pi*(xr+.3))
x4=1/4*sin(4*pi*(xr+.4))
v= x1+x2+x3+x4
plot(v ,pch='.',main="Signal")

write.csv(v,'signal.csv',row.names=FALSE)

tsRep <- ts(data=v)

lagmax = 200
acfv <- acf(tsRep,plot=FALSE, lag.max = lagmax)

#Java ACF From signal.csv
javaACF <-read.csv('java_acf.csv')

lines(javaACF[,2],javaACF[,1],col='red')
dir <-getwd()
plotName = "ACF.pdf"

pdf(plotName)

acf(tsRep,plot=TRUE, lag.max = lagmax)
lines(javaACF[,2],javaACF[,1],col='red')
dev.off()

###Calculate the short time fft
fftdata <- v


result <- stft(fftdata, win = wdw, inc = increment, coef=wdw/2,wtype="hanning.window")
plot(result$values)
result.feature <- result$values[,1:num_component]/wdw