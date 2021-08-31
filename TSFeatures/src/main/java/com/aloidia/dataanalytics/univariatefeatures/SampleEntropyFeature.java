package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class SampleEntropyFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name =  "Sample Entropy";

    public String getName(){return name;}

    @Override
    public String getDescription()
    {
        return "[SE (sample entropy)] is very similar to approximate entropy, used for determining the complexity of a time-series.";
    }
    @Override
    public Double getFeatureValue(){return value;}

    @Override
    public Double calculateFeature(TimeSeries data)
    {
        Double[] sample = data.readValue();

        Double[] v = null;

        if(sample.length>=500)
        {
            v= Arrays.copyOfRange(sample, 0, 500);
        }
        else
        {
            v= Arrays.copyOfRange(sample, 0, sample.length);
        }

        value = sample_entropy(v);

        return value;
    }

    //
    double sample_entropy(Double[] data) {

        //TODO edim is a parameter that defaults to 2
        // we use the default in FMA analysis but we may want to expose this in the future
        int edim = 2;

        //TODO r is a parameter that defaults to .2* sd
        // we use the default in FMA analysis but we may want to expose this in the future
        double r = Double.NaN;

        DescriptiveStatistics stats = new DescriptiveStatistics();
        // Add the data from the series to stats
        for (int i = 0; i < data.length; i++) {
            stats.addValue(data[i]);
        }
        double sd = stats.getStandardDeviation();

        r = .2 * sd;

        int N = data.length;

        double[] correl = new double[2];

        if ((N - edim)<1)
            return Double.NaN;

        double[][] dataMat = new double[edim + 1][N - edim];
        for (int i = 0; i < edim + 1; i++) {
            Double[] v = Arrays.copyOfRange(data, i, N - edim + i);
            double[] d = ArrayUtils.toPrimitive(v);
            dataMat[i] = d;
        }


        for (int m = edim; m < edim + 2; m++) {
            double[] count = new double[N - edim];
            Arrays.fill(count, 0.0);

            double[][] tempmat = new double[m][N-edim];
            for(int p=0;p<m;p++)
            {
                for(int q=0;q<N-edim;q++)
                {
                    tempmat[p][q]=dataMat[p][q];
                }
            }

            for(int i =0;i<N-m-1;i++) {

                double[] colDataMat = new double[m];
                for (int k = 0; k < m ; k++) {
                    colDataMat[k] = tempmat[k][i];
                }

                double[][] dataMatTile = new double[m][N - edim - i - 1];
                for (int k = 0; k < (N - edim - i - 1); k++) {
                    for (int l = 0; l < m; l++) {
                        dataMatTile[l][k] = colDataMat[l];
                    }
                }

                double[][] X= new double[m][N - edim - i - 1];

                try
                {
                    for(int p=0;p<m;p++)
                    {
                        for(int q=i+1;q< N - edim ;q++)
                        {
                            X[p][q-i-1]= Math.abs(tempmat[p][q]-dataMatTile[p][q-i-1]);
                        }
                    }
                }
                catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                }

                double[] dst= new double[N - edim - i - 1];
                for (int k = 0; k < (N - edim - i - 1); k++)
                {
                    double[] col = new double[m];
                    for(int p=0;p<m;p++)
                    {
                        col[p]=X[p][k];
                    }
                    Arrays.sort(col);
                    double max = col[col.length - 1];
                    dst[k]=max;
                }

                boolean[] boolVec = new boolean[N - edim - i - 1];
                for(int q=0;q<N - edim - i - 1 ;q++)
                {
                    boolVec[q] = dst[q] <= r ? true : false;
                }

                int boolVecSum=0;
                for(int q=0;q<N - edim - i - 1;q++)
                {
                    int bVal = boolVec[q] ? 1 : 0;
                    boolVecSum += bVal;
                }

                count[i]= (double)boolVecSum/(N-edim);

            }

            double countSum = 0;
            for(int p=0;p<N-edim;p++ ) {
                countSum += count[p];
            }

            correl[m-edim] = (double)countSum / (N-edim);
        }

        double sentropy = Math.log(correl[0] / correl[1]);

        return sentropy;
    }

}