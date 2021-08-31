package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SumFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name = "Sum";

    public String getName()
    {
        return name ;
    }

    @Override
    public String getDescription()
    {
        return "[Sum] represents the total net reads. This is calculated by adding up all of the readings";
    }
    @Override
    public Double getFeatureValue(){return value;}

    @Override
    public Double calculateFeature(TimeSeries data)
    {
        DescriptiveStatistics stats = new DescriptiveStatistics();

        //TODO MB CR: again, justify the use of the array over a List implementation
        Double[] sample = data.readValue();

        // Add the data from the series to stats
        for (int i = 0; i < sample.length; i++) {
            stats.addValue(sample[i]);
        }

        value = stats.getSum();

        return value;
    }
}
