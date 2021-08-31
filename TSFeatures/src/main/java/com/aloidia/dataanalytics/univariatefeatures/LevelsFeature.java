package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;

public class LevelsFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name = "LevelsFeature";

    public String getName() {
        return name;
    }

    @Override
    public String getDescription()
    {
        return "[Levels] represents the number of unique values";
    }

    @Override
    public Double getFeatureValue() {
        return value;
    }

    @Override
    public Double calculateFeature(TimeSeries data) {
        DescriptiveStatistics stats = new DescriptiveStatistics();

        Double[] sample = data.readValue();

        // Add the data from the series to stats
        for (int i = 0; i < sample.length; i++) {
            stats.addValue(sample[i]);
        }

        List<Double> readList = Arrays.asList(sample);

        Map<Double, Integer> frequencyMap = new HashMap<Double, Integer>();
        for (Double readValue : readList) {
            frequencyMap.put(readValue, Collections.frequency(readList, readValue));
        }

        value = Double.valueOf(frequencyMap.size());

        return value;
    }
}