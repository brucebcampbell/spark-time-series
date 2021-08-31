package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;

public interface UnivariateFeature {
    String getName();

    String getDescription();

    Double getFeatureValue();

    Double calculateFeature(TimeSeries data);
}
