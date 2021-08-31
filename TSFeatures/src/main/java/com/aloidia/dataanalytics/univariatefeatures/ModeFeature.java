package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;

public class ModeFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name = "ModeFeature";

    public String getName() {
        return name;
    }

    @Override
    public String getDescription()
    {
        return "[Mode] represents the most common reading";
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

        Map.Entry maxEntry = null;

        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo((Integer) maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }

        value = (Double) maxEntry.getKey();

        return value;

    }
}
