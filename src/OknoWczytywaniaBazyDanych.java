package src;

import javax.swing.*;
import java.awt.*;

public class OknoWczytywaniaBazyDanych extends JFrame {
    JPanel panelWewnetrzny;
    OknoWczytywaniaBazyDanych(){
        this.setSize(new Dimension(600,400));
        this.setResizable(false);
        this.setVisible(true);
        panelWewnetrzny= new JPanel();
    }
}
