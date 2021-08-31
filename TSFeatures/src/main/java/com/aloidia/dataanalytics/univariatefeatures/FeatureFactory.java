package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;

import java.util.*;

//User adds features and then calls getAllFeatures to calculate requested features.  Values are cached in each features for reuse.
//Features are embedded classes managed by FeatureFactory.  To add a new feature all that is needed is a derivative of UnivariateFeature.
public class FeatureFactory
{

    public FeatureFactory()
    {
        featureSet = new HashSet<UnivariateFeature>();
    }

    protected HashSet< UnivariateFeature> featureSet;

    public void addFeature(UnivariateFeature feature)
    {
        featureSet.add(feature);
    }

    public void addAllFeatures()
    {
        SumFeature sumF = new SumFeature();
        addFeature(sumF );

        MinFeature minF = new MinFeature();
        addFeature(minF);

        MaxFeature maxF = new MaxFeature();
        addFeature(maxF);

        MeanFeature meanF = new MeanFeature();
        addFeature(meanF);

        VarFeature varF = new VarFeature();
        addFeature(varF);

        SdFeature sdF = new SdFeature();
        addFeature(sdF);

        SkewFeature skewF = new SkewFeature();
        addFeature(skewF);

        KurtosisFeature kurtosisF = new KurtosisFeature();
        addFeature(kurtosisF);

        LevelsFeature levelF = new LevelsFeature();
        addFeature(levelF);

        ModeFeature modeF = new ModeFeature();
        addFeature(modeF);

        SampleCountFeature countF = new SampleCountFeature();
        addFeature(countF);

        MedianFeature medianF  = new MedianFeature();
        addFeature(medianF);

        FifthPercentileFeature fifthF = new FifthPercentileFeature();
        addFeature(fifthF);

        NinetyFifthPercentileFeature ninetyFithF = new NinetyFifthPercentileFeature();
        addFeature(ninetyFithF);

        PercentileFeature percentileF = new PercentileFeature(75.0);
        addFeature(percentileF);

        SampleEntropyFeature sampleentropyF = new SampleEntropyFeature();
        addFeature(sampleentropyF);

        ApproximateEntropyFeature entropyF = new ApproximateEntropyFeature();
        addFeature(entropyF);

    }

    public Double[] getFeatureVector(TimeSeries data) throws Exception {
        if(featureSet.size() ==0)
            throw new Exception("Error No features added to FeatureFactory in FeatureFactory::getFeatureVector");

        if(data.readValue().length <10)
            throw new Exception("Error need at least 10 sample to calculate time series features FeatureFactory in FeatureFactory::getFeatureMap");

        List<Double> featureList = new ArrayList<Double>();

        for (UnivariateFeature feature : featureSet)
        {
            feature.calculateFeature(data);

            Double value = feature.getFeatureValue();
            featureList.add(value);
        }

        Double[] result = new Double[featureList.size() ];

        featureList.toArray(result);

        return  result;
    }

    public HashMap<String, Double> getFeatureMap(TimeSeries data) throws Exception {
        if(featureSet.size() ==0)
            throw new Exception("Error No features added to FeatureFactory in FeatureFactory::getFeatureMap");

        if(data.readValue().length <10)
            throw new Exception("Error need at least 10 sample to calculate time series features FeatureFactory in FeatureFactory::getFeatureMap");

        HashMap<String, Double> featureList = new HashMap<String, Double>();

        for (UnivariateFeature feature : featureSet)
        {
            feature.calculateFeature(data);

            Double value = feature.getFeatureValue();

            featureList.put(feature.getName(),value);
        }

        return  featureList;
    }

}

