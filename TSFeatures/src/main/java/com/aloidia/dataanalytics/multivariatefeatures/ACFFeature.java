package com.aloidia.datascience.multivariatefeatures;


import com.aloidia.datascience.TimeSeries;
import com.aloidia.datascience.univariatefeatures.MeanFeature;
import com.aloidia.datascience.univariatefeatures.SdFeature;
import com.aloidia.datascience.univariatefeatures.VarFeature;

import java.util.Arrays;

public class ACFFeature implements  MultivariateFeature{

    Double[] value = null;

    static String name = "ACF";

    protected int lag= 200;

    public int getLag()
    {
        return lag;
    }

    public void setLag(int lag)
    {
        this.lag = lag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Autocorrelation";
    }

    @Override
    public Double[] getFeatureValue(){return value;}


    @Override
    public Double[] calculateFeature(TimeSeries data) {

        MeanFeature meanFeature = new MeanFeature();
        Double mean = meanFeature.calculateFeature(data);

        VarFeature varFeature = new VarFeature();
        Double var = varFeature.calculateFeature(data);
        Double[] readValues = data.readValue();

        Double[] acf = new Double[lag];

        bruteForceAutoCorrelation(readValues,acf, mean,var,lag);

        value=acf;

        return acf;
    }

    public void bruteForceAutoCorrelation(Double [] x, Double [] ac,Double mean, Double var, int lag) {
        Arrays.fill(ac, 0.0);
        int length = x.length;

        for (int j = 0; j < lag; j++) {
            for (int i = 0; i < length-j; i++) {
                ac[j] += (x[i] -mean) * ( x[(i+j)] - mean) ;
            }
            ac[j] = ac[j]/((length-j)*var);

        }
    }
}
