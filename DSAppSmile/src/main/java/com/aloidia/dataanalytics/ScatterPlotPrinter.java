package com.aloidia.datascience;

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

public class ScatterPlotPrinter extends PlotPanel {

    protected int width;

    protected int height;

    /*
    This is a 2D scatter plot class that takes a label index,width and height and prints the
     plot to a png file with the specified file name.
    This only supports 9 classes for now. Class labels greater than 9 get plotted in black
     */
public ScatterPlotPrinter(double[][] data, int[] label, String fileName, int width, int height) throws Exception {
    this.width=width;

    this .height= height;

    PlotCanvas canvas = smile.plot.ScatterPlot.plot(data);
    canvas.setTitle("Scatter Plot");
    //canvas.setAxisLabels("X Axis");
    add(canvas);

    if(data.length != label.length)
    {
        throw new Exception("data.length != label.length in " + this.toString() );
    }

//    if(data[0].length != 2)
//    {
//        throw new Exception("Need 2D data in ScatterPlotPrinter data[0].length != 2 " + this.toString() );
//    }

    for(int i = 0;i<data.length;i++)
    {
        double[][] datav = new double[1][2];

        datav[0]=data[i];
        switch (label[i]) {
            case 0:
                canvas.points(datav, '.', Color.RED);
                break;
            case 1:
                canvas.points(datav, '.', Color.BLUE);
                break;
            case 2:
                canvas.points(datav, '.', Color.GREEN);
                break;
            case 3:
                canvas.points(datav, '.', Color.CYAN);
                break;
            case 4:
                canvas.points(datav, '.', Color.MAGENTA);
                break;
            case 5:
                canvas.points(datav, '.', Color.ORANGE);
                break;
            case 6:
                canvas.points(datav, '.', Color.PINK);
                break;
            case 7:
                canvas.points(datav, '.', Color.YELLOW);
                break;
            case 8:
                canvas.points(datav, '.', Color.DARK_GRAY);
                break;
            default:
                canvas.points(datav, '.', Color.BLACK);

        }
    }


    JFrame frame = new JFrame("Box Plot");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(width, height));
    frame.setLocationRelativeTo(null);
    frame.setLocationRelativeTo(null);
    frame.getContentPane().add(this,BorderLayout.CENTER);
    frame.setVisible(true);
    this.setVisible(true);
    try {
        Thread.sleep(8000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    try {
        this.save(fileName);
    } catch (IOException e) {
        e.printStackTrace();
    }
    try {
        Thread.sleep(8000);
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
        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);// new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        this.printAll(g2d);

        ImageIO.write(bi, FileChooser.getExtension(file), file);
    }

}
