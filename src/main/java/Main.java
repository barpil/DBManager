import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            // Ustawienie FlatLaf (wersja jasna)
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        uruchom();
    }

    private static void uruchom(){
        OknoGlowne.getOknoGlowne();
        OknoWczytywaniaBazyDanych.showOknoWczytywania();

    }
}
