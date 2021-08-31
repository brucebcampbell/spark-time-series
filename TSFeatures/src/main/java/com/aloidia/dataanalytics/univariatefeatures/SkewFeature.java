package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SkewFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name = "Skewness";

    public String getName(){return name;}

    @Override
    public String getDescription()
    {
        return "[Skew] quantifies the asymmetry of the reading's distribution";
    }
    @Override
    public Double getFeatureValue(){return value;}

    @Override
    public Double calculateFeature(TimeSeries data)
    {
        DescriptiveStatistics stats = new DescriptiveStatistics();

        Double[] sample = data.readValue();

        // Add the data from the series to stats
        for (int i = 0; i < sample.length; i++) {
            stats.addValue(sample[i]);
        }

        value =stats.getSkewness();

        return value;
    }
}
