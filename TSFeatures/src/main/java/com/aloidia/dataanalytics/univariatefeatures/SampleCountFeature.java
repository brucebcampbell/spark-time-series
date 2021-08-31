package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SampleCountFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name = "Sample Count";

    public String getName(){return name;}

    @Override
    public String getDescription() {
        return null;
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
        value = Double.valueOf(sample.length);

        return value;
    }
}