import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger(Main.class);

        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
            log.debug("Flatlaf applied");
        } catch (Exception e) {
            log.error("Problem with applying Flatlaf");
        }
        uruchom();
    }

    private static void uruchom(){
        OknoGlowne.getOknoGlowne();
        OknoWczytywaniaBazyDanych.showOknoWczytywania();

    }
}
