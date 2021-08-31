package com.aloidia.datascience;

import org.junit.*;

import static org.junit.Assert.*;

import org.apache.commons.csv.*;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Time;
import java.util.*;

import org.apache.commons.csv.*;


public class ClientTest {


    private final static Logger log = Logger
            .getLogger(SummaryFeatures.class);

    @Test
    public void ClientTest() throws Exception {

        log.info("Starting SummaryFeatures Client Test");

        log.info("Config Info");

        ClassLoader classLoader = getClass().getClassLoader();
        URL resourceString = classLoader.getResource("water_time_series.csv");
        File file = new File(classLoader.getResource("water_time_series.csv").getFile());


        TimeSeries ts = fillTestDataPath(file.getAbsolutePath());
        ts.setEndpointId(31415926);

        SummaryFeatures summaryFeatures = new SummaryFeatures(ts);

        HashMap<String, Double> featureMap = new HashMap<String, Double>();
        try {
            featureMap = summaryFeatures.calculateFeatures();
        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }

        Set<Map.Entry<String, Double>> features = featureMap.entrySet();

        for (Map.Entry<String, Double> feature : features) {
            String featureName = feature.getKey();
            Double featureValue = feature.getValue();

            System.out.println("Featre " + featureName + " = " + featureValue);
        }
    }

    protected final TimeSeries fillTestDataPath(String filePath) {
        TimeSeries timeSeries = new TimeSeries();

        File csvData = new File(filePath);
        Charset charset = Charset.defaultCharset();
        CSVParser parser = null;

        try {
            parser = CSVParser.parse(csvData, charset, CSVFormat.RFC4180);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (CSVRecord csvRecord : parser) {
            //TODO which is it (readValue , samplePoint) or the other way around! Choose.
            timeSeries.addPoint(Integer.parseInt(csvRecord.get(1)), Double.parseDouble(csvRecord.get(0)));
        }

        return timeSeries;
    }

}
