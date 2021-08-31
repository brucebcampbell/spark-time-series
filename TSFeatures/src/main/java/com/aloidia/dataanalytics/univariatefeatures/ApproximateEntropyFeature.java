package com.aloidia.datascience.univariatefeatures;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class ApproximateEntropyFeature implements UnivariateFeature
{
    Double value = Double.NaN;

    static String name =  "Approximate Entropy";

    public String getName(){return name;}

    @Override
    public String getDescription()
    {
        return "[AE (approximate entropy)] quantifies the regularity and unpredictability of the time series data. A more regular time-series can predict the next term given the previous trends. Greater values representing a less predictable time-series";
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

        value = approx_entropy(v);

        return value;
    }

    double approx_entropy(Double[] data) {
        double edim=2;

        //TODO r is a parameter that defaults to .2* sd
        // we use the default in FMA analysis but we may want to expose this in the future
        double r=Double.NaN;

        DescriptiveStatistics stats = new DescriptiveStatistics();
        // Add the data from the series to stats
        for (int i = 0; i < data.length; i++) {
            stats.addValue(data[i]);
        }
        double sd =stats.getStandardDeviation();

        r = .2 *  sd;

        int N = data.length;

        double[] result =new double[2];

        for (int j=0; j< 2;j++)
        {
            int m =(int) edim + j;
            double[] phi =new double[(N - m + 1)];
            double[][] dataMat = new double[m][ N - m + 1];

            for(int i=0;i<m;i++) {

                Double[] v= Arrays.copyOfRange(data, i, N - m + i + 1);
                double[] d = ArrayUtils.toPrimitive(v);
                dataMat[i]=d;    //data[i:N - m + i + 1];
            }

            for (int i=0;i<N-m+1;i++) {//i in xrange(0, N - m + 1):
                //Take the col and tile it
                double[] colDataMat = new double[m];
                for(int k=0;k<m;k++)
                {
                    colDataMat[k]=dataMat[k][i];
                }

                double[][] dataMatTile = new double[m][ N - m + 1];
                for(int k=0;k<(N-m+1);k++)
                {
                    for(int l=0;l<m;l++)
                    {
                        dataMatTile[l][k]=colDataMat[l];
                    }
                }

                double[][] tempMat= new double[m][N-m+1];
                for(int p=0;p<m;p++)
                {
                    for(int q=0;q<N-m+1;q++)
                    {
                        tempMat[p][q]= Math.abs(dataMat[p][q]-dataMatTile[p][q]);
                    }
                }

                boolean[][] boolMat = new boolean[m][N-m+1];
                for(int p=0;p<m;p++)
                {
                    for(int q=0;q<N-m+1;q++)
                    {
                        boolMat[p][q]=  tempMat[p][q]<= r ? true : false;  //TODO revisit if this is correct
                    }
                }

                boolean[] boolMatAllCol = new boolean[N-m+1];
                for(int q=0;q<N-m+1;q++)
                {
                    boolMatAllCol[q]=true;
                    for(int p=0;p<m;p++)
                    {
                        if(boolMatAllCol[q]==true && boolMat[p][q]==true)
                            boolMatAllCol[q]=true;
                        else
                        {
                            boolMatAllCol[q]=false;
                            break;
                        }
                    }
                }

                int boolMatSum=0;
                for(int q=0;q<N-m+1;q++)
                {
                        int bVal = boolMatAllCol[q] ? 1:0;
                        boolMatSum+= bVal;
                }

                phi[i] = ((boolMatSum) * 1.0 / (N - m + 1));

                double resultJ =0;
                for(int q=0;q<N-m+1;q++)
                {
                    resultJ += phi[q];
                }

                result[j] = resultJ/ (N - m + 1);
            }
        }
        double apen = Math.log(result[0] / result[1]);

        return apen;
    }

}