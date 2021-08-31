package com.aloidia.datascience.multivariatefeatures;

import com.aloidia.datascience.TimeSeries;

public interface MultivariateFeature {
    String getName();

    String getDescription();

    Double[] getFeatureValue();

    Double[] calculateFeature(TimeSeries data);
}
