package com.aloidia.datascience.univariatefeatures;


import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SdFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name = "Standard Deviation";

    public String getName(){return name;}

    @Override
    public String getDescription()
    {
        return "[Sd (Standard deviation)] quantifies the variation of the reading's distribution. Values close to 0 indicate that the data is close to the mean while greater values represent more spread in the data";
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

        value =stats.getStandardDeviation();

        return value;
    }
}
