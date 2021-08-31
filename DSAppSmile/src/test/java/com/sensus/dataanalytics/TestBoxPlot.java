package com.aloidia.datascience;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;
import smile.plot.BoxPlot;
import smile.plot.PlotCanvas;
import smile.swing.FileChooser;
import smile.swing.Printer;


    public class TestBoxPlot extends JPanel implements Printable {

    @Test
    public void runTestBoxPlot()
    {
        JFrame frame = new JFrame("Box Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1400, 1400));
        frame.setLocationRelativeTo(null);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(this,BorderLayout.CENTER);
        frame.setVisible(true);
        this.setVisible(true);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            this.save("box_plot_test.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setVisible(false);
        frame.remove(this);
        frame.setVisible(false);
        frame.removeAll();
        frame.dispose();

    }
    public TestBoxPlot() {

        super(new GridLayout(1, 2));

        double[][] data = new double[5][100];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                double x, y, r;
                do {
                    x = 2 * (Math.random() - 0.5);
                    y = 2 * (Math.random() - 0.5);
                    r = x * x + y * y;
                } while (r >= 1.0);

                double z = Math.sqrt(-2.0 * Math.log(r) / r);
                data[i][j] = new Double(x * z);
            }
        }

        PlotCanvas canvas = BoxPlot.plot(data, new String[]{"Group A", "Group B", "Big Group C", "Group D", "Very Long Group E"});
        canvas.setTitle("Box Plot A");
        canvas.getAxis(0).setRotation(-Math.PI / 2);
        add(canvas);

        canvas = BoxPlot.plot(data[0]);
        canvas.setTitle("Box Plot B");
        add(canvas);
    }
    @Override
    public int print(java.awt.Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            // We have only one page, and 'page' is zero-based
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;

        // User (0,0) is typically outside the imageable area, so we must
        // translate by the X and Y values in the PageFormat to avoid clipping
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // Scale plots to paper size.
        double scaleX = pf.getImageableWidth() / this.getWidth();
        double scaleY = pf.getImageableHeight() / this.getHeight();
        g2d.scale(scaleX, scaleY);

        // Disable double buffering
        RepaintManager currentManager = RepaintManager.currentManager(this);
        currentManager.setDoubleBufferingEnabled(false);

        // Now we perform our rendering
        this.printAll(g);

        // Enable double buffering
        currentManager.setDoubleBufferingEnabled(true);

        // tell the caller that this page is part of the printed document
        return PAGE_EXISTS;
    }

    public void save(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);// new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        this.printAll(g2d);

        ImageIO.write(bi, FileChooser.getExtension(file), file);
    }



}


