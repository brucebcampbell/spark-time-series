package com.aloidia.datascience;

import org.apache.commons.lang3.ArrayUtils;
import smile.plot.LinePlot;
import smile.plot.PlotCanvas;
import smile.plot.PlotPanel;
import smile.plot.ScatterPlot;
import smile.swing.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.aloidia.datascience.TimeSeries;

public class TimeSeriesPlot extends PlotPanel {

    protected int width;

    protected int height;

    protected TimeSeries ts;

    protected String key;

    protected String fileName;

    public TimeSeriesPlot(TimeSeries ts, String fileName, String key,int width, int height) throws Exception {

        this.width=width;

        this.height= height;

        this.key = key;

        this.ts = ts;

        this.fileName = fileName;
    }

    public void render()
    {
        //Get the data and plot
        double[] data =  ArrayUtils.toPrimitive(ts.readValue());
        PlotCanvas canvas = LinePlot.plot(data);
        canvas.setTitle(key);
        add(canvas);

        JFrame frame = new JFrame(key);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(width, height));
        frame.setLocationRelativeTo(null);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(this,BorderLayout.CENTER);
        frame.setVisible(true);
        this.setVisible(true);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            this.save(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.setVisible(false);
        frame.remove(this);
        frame.setVisible(false);
        frame.removeAll();
        frame.dispose();

    }

    public void save(String fileName) throws IOException {
        File file = new File(fileName);

        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        this.printAll(g2d);
        ImageIO.write(bi, FileChooser.getExtension(file), file);
    }

}
