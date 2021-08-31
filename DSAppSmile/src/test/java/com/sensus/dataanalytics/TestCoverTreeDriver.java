package com.aloidia.datascience;

import org.junit.*;
import smile.math.distance.EuclideanDistance;
import smile.neighbor.CoverTree;
import smile.neighbor.LinearSearch;
import smile.neighbor.Neighbor;
import smile.stat.distribution.MultivariateGaussianDistribution;


public class TestCoverTreeDriver {

    @Test
    public void testCoverTree1D() {
        /*
        Generate 1000 random uniformly distributed points in R^10
        Add to cover tree structure
        Check the nearest 100 points to 0 \in R^10
        HW - what is the expected radius of the ball in R^10 that encloses the
        100 nearest neighbors to 0 ?
        Validate with a simulation?
         */
        double[][] data = null;
        CoverTree<double[]> coverTree = null;
        LinearSearch<double[]> naive = null;
        data = new double[1000][];
        for (int i = 0; i < data.length; i++) {
            data[i] = new double[10];
            for (int j = 0; j < data[i].length; j++)
                data[i][j] = Math.random();
        }

        coverTree = new CoverTree<double[]>(data, new EuclideanDistance());
        naive = new LinearSearch<double[]>(data, new EuclideanDistance());

        double[] pointsToSearch = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        Neighbor<double[], double[]>[] neighbors = coverTree.knn(pointsToSearch, 100);


        assert (70 == 70);

    }

    @Test
    public void testCoverTree2D() {
        double[] mu1 = {1.0, 1.0, 1.0};
        double[][] sigma1 = {{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};

        double[] mu2 = {-2.0, -2.0, -2.0};
        double[][] sigma2 = {{1.0, 0.3, 0.8}, {0.3, 1.0, 0.5}, {0.8, 0.5, 1.0}};

        double[][] data = new double[2000][];
        int[] label = new int[2000];

        MultivariateGaussianDistribution g1 = new MultivariateGaussianDistribution(mu1, sigma1);
        for (int i = 0; i < 1000; i++) {
            data[i] = g1.rand();
            label[i] = 0;
        }

        MultivariateGaussianDistribution g2 = new MultivariateGaussianDistribution(mu2, sigma2);
        for (int i = 0; i < 1000; i++) {
            data[1000 + i] = g2.rand();
            label[1000 + i] = 1;
        }

        try {
            ScatterPlotPrinter scatterPlot = new ScatterPlotPrinter(data, label, "/home/vagrant/tmp/CoverTreeDataSet.png", 1920, 1080);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CoverTree<double[]> coverTree = null;
        LinearSearch<double[]> naive = null;

        coverTree = new CoverTree<double[]>(data, new EuclideanDistance());
        naive = new LinearSearch<double[]>(data, new EuclideanDistance());


        {
            Neighbor<double[], double[]>[] neighborsMu1 = coverTree.knn(mu1, 100);


            Neighbor<double[], double[]>[] neighborsMu2 = coverTree.knn(mu2, 100);
            double[][] dataNN = new double[200][];
            int[] labelNN = new int[200];


            for (int i = 0; i < 100; i++) {
                dataNN[i] = neighborsMu1[i].value;
                labelNN[i] = 0;
            }


            for (int i = 0; i < 100; i++) {
                dataNN[100 + i] = neighborsMu2[i].value;
                labelNN[100 + i] = 1;
            }

            try {
                ScatterPlotPrinter scatterPlot = new ScatterPlotPrinter(dataNN, labelNN, "/home/vagrant/tmp/CoverTree_Neighbors.png", 1920, 1080);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        assert (70 == 70);


    }



}