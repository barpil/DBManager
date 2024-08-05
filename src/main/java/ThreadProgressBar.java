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
        przygotujProgressBar();
        PanelSterowania.getPanelSterowania().getPanelProgressBara().add(this);
        this.setVisible(true);
        System.out.println("Pokazano progress bar");
        PanelSterowania.getPanelSterowania().getPanelProgressBara().setBackground(Color.black);
        PanelSterowania.getPanelSterowania().revalidate();
        PanelSterowania.getPanelSterowania().repaint();
    }

    private void przygotujProgressBar(){
        this.setValue(MIN_VALUE);
        progressUnit= MAX_VALUE/BazaDanych.getBazaDanych().getSqlThreadQueue().liczbaPozostalychWatkow();
    }

    public void zwiekszProgress(){
        this.setValue(this.getValue()+progressUnit);
        System.out.println(getValue());
        if(this.getValue()>=MAX_VALUE){
            setVisible(false);
        }
    }
}
