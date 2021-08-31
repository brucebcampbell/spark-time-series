package com.aloidia.datascience;

import com.aloidia.datascience.multivariatefeatures.ACFFeature;
import com.aloidia.datascience.multivariatefeatures.MultivariateFeature;

import static com.aloidia.datascience.timeseriesutilities.TimeSeriesWriter.writeToCSVFile;

import org.apache.commons.csv.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class MultivariateFeaturesTest {

    @org.junit.Test
    public void testACFFeature() {

        System.out.println("--------Testing ACFFeature  ");
        TimeSeries timeSeries = new TimeSeries();

        Charset charset = Charset.defaultCharset();
        CSVParser parser = null;
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("signal.csv").getFile());
        String filename = file.getAbsolutePath();

        File csvData = new File(filename);

        try {
            parser = CSVParser.parse(csvData, charset, CSVFormat.RFC4180);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int tic =0;
        for (CSVRecord csvRecord : parser) {
            //TODO which is it (readValue , samplePoint) or the other way around! Choose.
            timeSeries.addPoint(tic,Double.parseDouble(csvRecord.get(0)));
            tic= tic+1;

        }

        ACFFeature acfFeature = new ACFFeature();

        acfFeature.setLag(200);

        Double[] acf = acfFeature.calculateFeature(timeSeries);

        tic =0;
        TimeSeries acfTimeSeries = new TimeSeries();
        for (int j=0;j<acf.length;j++) {
            acfTimeSeries.addPoint(tic, acf[j]);
            tic = tic + 1;
        }

        String fileName = "results/java_acf.csv";
        try {
            writeToCSVFile(acfTimeSeries, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
