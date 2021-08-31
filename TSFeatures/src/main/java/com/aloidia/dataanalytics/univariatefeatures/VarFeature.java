package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class VarFeature implements UnivariateFeature
{

    //TODO MB CR: make this private--nothing good will come out keeping this package private
    Double value = Double.NaN;

    //TODO MB CR: static class members should be final AND should be capitalized (by convention), but good on you for being kind to your string constants pool
    static String name = "Variance";

    //TODO MB CR: make up your mind: are you going to coddle your braces or let them live on their own?
    public String getName(){return name;}

    @Override
    public String getDescription()
    {
        return "[Variance] is a dimensional measure of sperad of the distribution of reads.  Variance is the square of Sd";
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

        value =stats.getVariance();

        return value;
    }
}