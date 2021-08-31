package com.aloidia.datascience;

import org.apache.commons.math.util.OpenIntToDoubleHashMap;
import org.junit.*;
import static org.junit.Assert.*;
import org.apache.commons.csv.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

public class TimeSeriesTest {

    @org.junit.Test
    public void testIterator() {

        System.out.println("--------Testing Iterator ");
        TimeSeries timeSeries = new TimeSeries();

        Charset charset = Charset.defaultCharset();
        CSVParser parser = null;
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("normal.csv").getFile());
        String filename = file.getAbsolutePath();

        File csvData = new File(filename);

        try {
            parser = CSVParser.parse(csvData, charset, CSVFormat.RFC4180);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (CSVRecord csvRecord : parser) {
            //TODO which is it (readValue , samplePoint) or the other way around! Choose.
            timeSeries.addPoint(Integer.parseInt(csvRecord.get(0)), Double.parseDouble(csvRecord.get(1)));
        }

        Iterator< Map<Integer,Double> > it = timeSeries.getIterator();

        StringBuilder sb = new StringBuilder();

        Map.Entry first = (Map.Entry) it.next();
        Integer firstKey = (Integer) first.getKey();
        Double firstValue = (Double) first.getValue();
        assertTrue(firstKey == 1);
        assertTrue(firstValue == 0.0601342758250835);

        while ( it.hasNext() ) {
            Map.Entry entry = (Map.Entry) it.next();
            Integer key = (Integer) entry.getKey();
            Double value = (Double) entry.getValue();
            sb.append(key + "," + value + ";");
        }//while

        String result = sb.toString();


    }
}