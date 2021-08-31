package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class MinFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name = "Min";

    public String getName(){return name;}

    @Override
    public String getDescription()
    {
        return "[Min] represents the  smallest reading";
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

        value = stats.getMin();

        return value;
    }
}
