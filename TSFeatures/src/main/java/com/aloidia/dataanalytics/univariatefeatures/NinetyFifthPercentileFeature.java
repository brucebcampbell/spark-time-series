package com.aloidia.datascience.univariatefeatures;


import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class NinetyFifthPercentileFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name = "Ninety Fifth Percentile";

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

        value =stats.getPercentile(95);

        return value;
    }
}
