package com.aloidia.datascience.timeseriesutilities;

import com.aloidia.datascience.TimeSeries;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vagrant on 10/10/16.
 */
public class TimeSeriesWriter {

    public static boolean writeToCSVFile(TimeSeries timeSeries, String path) throws IOException {
        List<String> header= new ArrayList<String>();
        header.add("sample_point");
        header.add("read_value");

        try {
            if (path != null) {
                FileWriter fw = new FileWriter(new File(path));
                CSVPrinter printer = CSVFormat.newFormat(',')
                        .withIgnoreSurroundingSpaces().withAllowMissingColumnNames().withRecordSeparator("\n").print(fw);
                for (String head : header) {
                    printer.print(head);
                }
                printer.println();
                Double[] readValues = timeSeries.readValue();
                Integer[] samplePoints = timeSeries.samplePoint();

                for (int i = 0; i < readValues.length; i++) {
                    printer.printRecord(readValues[i],samplePoints[i]);

                    printer.flush();
                }
                printer.close();
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
