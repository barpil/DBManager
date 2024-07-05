package src;

import javax.swing.*;
import java.sql.SQLException;
import java.util.LinkedList;

public class Menu extends JMenuBar {
    Menu(){
        dodajComponenty();
    }

    private void dodajComponenty() {
        JMenu menuGlowne = new JMenu("Menu");
        this.add(menuGlowne);
        JMenuItem zaktualizujIdMI = new JMenuItem("Zaktualizuj ID");
        zaktualizujIdMI.addActionListener(e -> BazaDanych.getBazaDanych().zaktualizujID());
        menuGlowne.add(zaktualizujIdMI);
        JMenu sortujWyniki = new JMenu("Sortuj...");
        menuGlowne.add(sortujWyniki);
        JMenu sortujASC = new JMenu("ASC");
        JMenu sortujDESC = new JMenu("DESC");
        sortujWyniki.add(sortujASC);
        sortujWyniki.add(sortujDESC);

        LinkedList<String> listaKolumn;
        try {
            listaKolumn = BazaDanych.getBazaDanych().getNazwyKolumn();
            for(String nazwaKolumny: listaKolumn){
                JMenuItem przyciskKolumnyASC = new JMenuItem(nazwaKolumny);
                przyciskKolumnyASC.addActionListener(e -> {
                    try {
                        BazaDanych.getBazaDanych().sortujDane(nazwaKolumny,"ASC");
                        PanelElementow.getPanelElementow().updateModel();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                JMenuItem przyciskKolumnyDESC = new JMenuItem(nazwaKolumny);
                przyciskKolumnyDESC.addActionListener(e -> {
                    try {
                        BazaDanych.getBazaDanych().sortujDane(nazwaKolumny,"DESC");
                        PanelElementow.getPanelElementow().updateModel();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                sortujASC.add(przyciskKolumnyASC);
                sortujDESC.add(przyciskKolumnyDESC);
                System.out.println("dodano "+nazwaKolumny);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
