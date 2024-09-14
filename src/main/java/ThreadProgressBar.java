import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class ThreadProgressBar extends JProgressBar {
    final private int MIN_VALUE = 0;
    final private int MAX_VALUE = 100;

    private int progressUnit;

    ThreadProgressBar(){

            this.setMinimumSize(new Dimension(80, 20));
            this.setMaximumSize(new Dimension(80,20));
            this.setMinimum(MIN_VALUE);
            this.setMaximum(MAX_VALUE);

            this.setStringPainted(true);
            this.setVisible(false);




    }

    public void przygotujProgressBar() {
        if (BazaDanych.getBazaDanych().getSqlThreadQueue().liczbaPozostalychWatkow()!=0) {
            this.setVisible(true);
            this.setValue(MIN_VALUE);
            progressUnit = MAX_VALUE / BazaDanych.getBazaDanych().getSqlThreadQueue().liczbaPozostalychWatkow();
            this.repaint();
        }
    }

    public void zwiekszProgress() {

        this.setValue(this.getValue() + progressUnit);
        SwingUtilities.invokeLater(() ->{
            this.revalidate();
            this.repaint();
            if (this.getValue() >= MAX_VALUE) {
                try {
                    Thread.sleep(500);
                    this.setVisible(false);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }
}
