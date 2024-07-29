import javax.swing.*;
import java.awt.*;

public class ThreadProgressBar extends JProgressBar {
    final private int MIN_VALUE = 0;
    final private int MAX_VALUE = 100;

    private int progressUnit;

    ThreadProgressBar(){
        this.setMinimumSize(new Dimension(150,50));
        this.setMinimum(MIN_VALUE);
        this.setMaximum(MAX_VALUE);

        this.setVisible(false);
    }

    public void przygotujProgressBar(){
        this.setValue(MIN_VALUE);
        progressUnit= MAX_VALUE/BazaDanych.getBazaDanych().getSqlThreadQueue().liczbaPozostalychWatkow();
        //Dokonczyc przygotowywanie progressBara

    }
}
