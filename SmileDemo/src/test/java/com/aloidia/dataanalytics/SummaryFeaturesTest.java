package com.aloidia.datascience;

import org.junit.*;
import static org.junit.Assert.*;
import org.apache.commons.csv.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SummaryFeaturesTest  {

    //Populates a map with the test data from file
    public HashMap<String, Double> testUnivariate(File csvData) {
        TimeSeries timeSeries = new TimeSeries();

        Charset charset = Charset.defaultCharset();
        CSVParser parser = null;

        try {
            parser = CSVParser.parse(csvData, charset, CSVFormat.RFC4180);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (CSVRecord csvRecord : parser) {
            //TODO which is it (readValue , samplePoint) or the other way around! Choose.
            timeSeries.addPoint(Integer.parseInt(csvRecord.get(0)), Double.parseDouble(csvRecord.get(1)));
        }

        SummaryFeatures summaryFeatures = new SummaryFeatures(timeSeries);

        HashMap<String, Double> featureMap = null;
        try {
            featureMap = summaryFeatures.calculateFeatures();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<Map.Entry<String, Double>> features = featureMap.entrySet();

        for (Map.Entry<String, Double> feature : features) {
            String featureName = feature.getKey();
            Double featureValue = feature.getValue();

            System.out.println("Feature " + featureName + " = " + featureValue);
        }

        return featureMap;
    }

    @org.junit.Test
    public void testNormal() {
        //System.out.println("Working Directory = " +  System.getProperty("user.dir"));

        System.out.println("--------Testing Normal ");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("normal.csv").getFile());
        String filename = file.getAbsolutePath();

        File csvData = new File(filename);

        HashMap<String, Double> featureMap = testUnivariate(csvData);

        assertTrue(featureMap.get("LevelsFeature") == 1000.0);
        assertTrue(featureMap.get("Sample Entropy") == 2.113993923186498);
        assertTrue(featureMap.get("75.0Percentile") == 0.6493944522398505);
        assertTrue(featureMap.get("Variance") == 1.0064811909451545);
        assertTrue(featureMap.get("Ninety Fifth Percentile") == 1.695287081900257);
        assertTrue(featureMap.get("Sample Count") == 1000.0);
        assertTrue(featureMap.get("Skewness") == 0.00904916092241807);
        assertTrue(featureMap.get("Sum") == -3.6634393112917056);
        assertTrue(featureMap.get("ModeFeature") == -0.806816453762797);
        assertTrue(featureMap.get("Kurtosis") == -0.05695091122587126);
        assertTrue(featureMap.get("Standard Deviation") == 1.003235361689945);
        assertTrue(featureMap.get("MedianFeature") == -0.0110326496001753);
        assertTrue(featureMap.get("Fifth Percentile") == -1.6715306761607294);
        assertTrue(featureMap.get("Max") == 2.97255373364312);
        assertTrue(featureMap.get("Min") == -3.33807025199818);
        assertTrue(featureMap.get("Mean") == -0.00366343931129165);
        assertTrue(featureMap.get("Approximate Entropy") == 1.4301542389256232);


        System.out.println("Working Directory = " + System.getProperty("user.dir"));

    }

    @org.junit.Test
    public void testPoisson() {
        System.out.println("--------Testing Poisson Lambda = 10 ");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("poisson_lambda_10.csv").getFile());
        String filename = file.getAbsolutePath();

        File csvData = new File(filename);

        HashMap<String, Double> featureMap = testUnivariate(csvData);

        assertTrue(featureMap.get("LevelsFeature") == 22.0);
        assertTrue(featureMap.get("Sample Entropy") == 2.207529118165038);
        assertTrue(featureMap.get("75.0Percentile") == 12.0);
        assertTrue(featureMap.get("Variance") == 10.15058958958958);
        assertTrue(featureMap.get("Sample Count") == 1000.0);
        assertTrue(featureMap.get("Ninety Fifth Percentile") == 16.0);
        assertTrue(featureMap.get("Skewness") == 0.33360431930456463);
        assertTrue(featureMap.get("Sum") == 10081.0);
        assertTrue(featureMap.get("ModeFeature") == 10.0);
        assertTrue(featureMap.get("Kurtosis") == 0.20291787888330992);
        assertTrue(featureMap.get("Standard Deviation") == 3.1859989939718405);
        assertTrue(featureMap.get("MedianFeature") == 10.0);
        assertTrue(featureMap.get("Fifth Percentile") == 5.0);
        assertTrue(featureMap.get("Max") == 22.0);
        assertTrue(featureMap.get("Min") == 1.0);
        assertTrue(featureMap.get("Mean") == 10.081);
        assertTrue(featureMap.get("Approximate Entropy") == 1.2846875197456726);
    }

    @org.junit.Test
    public void testUniform() {
        System.out.println("--------Testing Uniform ");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("uniform.csv").getFile());
        String filename = file.getAbsolutePath();

        File csvData = new File(filename);

        HashMap<String, Double> featureMap = testUnivariate(csvData);

        assertTrue(featureMap.get("LevelsFeature") == 1000.0);
        assertTrue(featureMap.get("Sample Entropy") == 2.1794599447770184);
        assertTrue(featureMap.get("75.0Percentile") == 0.75936649821233);
        assertTrue(featureMap.get("Variance") == 0.08151467140055726);
        assertTrue(featureMap.get("Ninety Fifth Percentile") == 0.9429260088130829);
        assertTrue(featureMap.get("Sample Count") == 1000.0);
        assertTrue(featureMap.get("Skewness") == 0.029682853217655823);
        assertTrue(featureMap.get("Sum") == 499.65631903219037);
        assertTrue(featureMap.get("ModeFeature") == 0.272254725452513);
        assertTrue(featureMap.get("Kurtosis") == -1.2144017840313217);
        assertTrue(featureMap.get("Standard Deviation") == 0.28550774315341654);
        assertTrue(featureMap.get("MedianFeature") == 0.49454465869348496);
        assertTrue(featureMap.get("Fifth Percentile") == 0.0586567498045042);
        assertTrue(featureMap.get("Max") == 0.999933788087219);
        assertTrue(featureMap.get("Mean") == 0.4996563190321904);
        assertTrue(featureMap.get("Min") == 0.00276626134291291);
        assertTrue(featureMap.get("Approximate Entropy") == 1.4631259925358915);
    }


    @org.junit.Test
    public void testConstant() {
        System.out.println("--------Testing Constant ");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("constant.csv").getFile());
        String filename = file.getAbsolutePath();

        File csvData = new File(filename);

        HashMap<String, Double> featureMap = testUnivariate(csvData);

        assertTrue(featureMap.get("LevelsFeature") == 1.0);
        assertTrue(featureMap.get("Sample Entropy") == 8.080644835536319E-6);
        assertTrue(featureMap.get("75.0Percentile") == 1.0);
        assertTrue(featureMap.get("Variance") == 0.0);
        assertTrue(featureMap.get("Ninety Fifth Percentile") == 1.0);
        assertTrue(featureMap.get("Sample Count") == 1000.0);
        assertTrue(featureMap.get("Skewness").isNaN() == true);
        assertTrue(featureMap.get("Sum") == 1000.0);
        assertTrue(featureMap.get("Kurtosis").isNaN() == true);
        assertTrue(featureMap.get("ModeFeature") == 1.0);
        assertTrue(featureMap.get("Standard Deviation") == 0.0);
        assertTrue(featureMap.get("MedianFeature") == 1.0);
        assertTrue(featureMap.get("Fifth Percentile") == 1.0);
        assertTrue(featureMap.get("Max") == 1.0);
        assertTrue(featureMap.get("Mean") == 1.0);
        assertTrue(featureMap.get("Min") == 1.0);
        assertTrue(featureMap.get("Approximate Entropy") == 0.0);
    }

    @Test
    public void testEdgeCases() {

        TimeSeries timeSeries = new TimeSeries();
        timeSeries.addPoint(0, 1.0);
        SummaryFeatures summaryFeatures = new SummaryFeatures(timeSeries);

        HashMap<String, Double> featureMap = null;
        try {
            featureMap = summaryFeatures.calculateFeatures();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("we expect an error since there are fewer than 10 points");
        }

    }

}
