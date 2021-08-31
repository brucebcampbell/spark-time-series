package com.aloidia.datascience.SmileDemo;

import smile.data.NominalAttribute;

import java.io.FileInputStream;
import java.text.ParseException;
import smile.data.parser.DelimitedTextParser;
import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import java.io.IOException;
import org.apache.log4j.Logger;

public class kNNExample {
  
	private final static Logger log = Logger
			.getLogger(kNNExample.class);

	public static void main(String[] args) {
	ArffParser arffParser = new ArffParser();
    arffParser.setResponseIndex(4);
        AttributeDataset weather = null;
        try {
            weather = arffParser.parse(new FileInputStream("data/weka/weather.nominal.arff"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double[][] x = weather.toArray(new double[weather.size()][]);
    int[] y = weather.toArray(new int[weather.size()]);

	log.info("Starting KNNExample Main");
}
}
