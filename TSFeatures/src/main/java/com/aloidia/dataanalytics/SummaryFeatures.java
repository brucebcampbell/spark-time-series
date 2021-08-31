package com.aloidia.datascience;

import com.aloidia.datascience.univariatefeatures.FeatureFactory;
import org.apache.log4j.Logger;

import java.util.*;

public class SummaryFeatures {

    private final static Logger log = Logger
            .getLogger(SummaryFeatures.class);

    private TimeSeries data;

    public SummaryFeatures(TimeSeries data)
    {
        this.data = new TimeSeries(data);
    }

    public HashMap<String,Double> calculateFeatures() throws Exception {

        HashMap<String,Double> featureMap = new HashMap<String,Double>();
        try {

            FeatureFactory featureFactory = new FeatureFactory();

            featureFactory.addAllFeatures();

            featureMap = featureFactory.getFeatureMap(data);

        }
        catch (Exception e)
        {
            log.error(e.getMessage());

            if (featureMap.isEmpty())
                throw new Exception("empty featureSet in SummaryFeatures::calculateFeatures" + e.getMessage());
        }

        return featureMap;
    }
}

