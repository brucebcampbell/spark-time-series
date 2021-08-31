This package is a Java library for client side analytics.  It's aim is to calulate univariate featuers of time series data. 

There are four classes of note 

1. __TSFeaturesDriver__ a driver that runs an example workflow

2. __TimeSeries__ is a class that manages time series data.  Internally it uses a  java util TreeMap to keep the data which is a A Red-Black tree based on the NavigableMap implementation. The map is sorted according to the natural ordering of its keys, or by a Comparator provided at map creation time, depending on which constructor is used.  This implementation provides guaranteed log(n) time cost for the containsKey, get, put and remove operations. 
The TimeSeries class currently uses double for read value and int for epoch time stamps.  The idea is that other date classes can be used for the container without chaning any of the code using the TimeSeries class uses by implementing a Comparator

3. __FeatureFactory__ is a class which manages a set of __UnivariateFeature__ subclasses which each implemnet a feature. Internally the feature data is kept in a map.  The user adds one or more features to the map for evaluation.


4. __SummaryFeatures__ is a class which manages the interaction between the __TimeSeries__ object and  the __FeatureFactory__.  It's not built out currently as it only implements an example workflow of calculating all the features for a time series.

