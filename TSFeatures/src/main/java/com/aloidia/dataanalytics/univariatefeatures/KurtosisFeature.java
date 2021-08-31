package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class KurtosisFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name =  "Kurtosis";

    public String getName(){return name;}

    @Override
    public String getDescription()
    {
        return "[Kurtosis] quantifies the peakedness or flatness of the reading's distribution. Negative values indicate a flatter distribution while greater values represent a more peaked distribution";
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

        value =stats.getKurtosis();

        return value;
    }
}