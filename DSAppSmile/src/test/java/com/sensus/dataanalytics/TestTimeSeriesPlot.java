package com.aloidia.datascience;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TestTimeSeriesPlot {

    @org.junit.Test
    public void testIterator() {

        System.out.println("--------Testing TimeSeriesPlot ");
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
        DSAppSimlePropertyManager props = new DSAppSimlePropertyManager();

        String fileLocation= null;
        try {
            fileLocation = props.getResultsLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String key="Test TimeSeries Plot";
        int width=1400;
        int height=1400;
        try {
            String fileName = fileLocation + "/" + "normal-test-data.png";

            TimeSeriesPlot tsp = new TimeSeriesPlot(timeSeries,  fileName,  key, width,  height);

            tsp.render();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
